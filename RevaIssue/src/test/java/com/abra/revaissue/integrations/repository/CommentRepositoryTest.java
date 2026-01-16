package com.abra.revaissue.integrations.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import com.abra.revaissue.entity.Comment;
import com.abra.revaissue.entity.Issue;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import com.abra.revaissue.enums.IssueStatus;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.CommentRepository;
import com.abra.revaissue.repository.IssueRepository;
import com.abra.revaissue.repository.ProjectRepository;
import com.abra.revaissue.repository.UserRepository;

@DataJpaTest
@TestPropertySource(locations = "classpath:test.properties")
public class CommentRepositoryTest {

    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private IssueRepository issueRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private ProjectRepository projectRepository;

    @Autowired
    public CommentRepositoryTest(CommentRepository commentRepository, UserRepository userRepository,
            IssueRepository issueRepository, BCryptPasswordEncoder passwordEncoder,
            ProjectRepository projectRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
    }

    @Test
    void findByIssue_IssueIdPositiveTest() {
        User user = new User();
        user.setUserName("admin");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setRole(Role.ADMIN);
        user = userRepository.save(user);
        Project project = new Project();
        project.setProjectName("Default Project");
        project.setProjectDescription("This is a default project.");
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());
        project.setCreatedByUserId(user.getUserId());
        Project savedProject = projectRepository.save(project);
        Issue issue = new Issue();
        issue.setName("Sample Issue");
        issue.setDescription("This is a sample issue for data loading.");
        issue.setStatus(IssueStatus.OPEN);
        issue.setSeverity(IssueSeverity.LOW);
        issue.setPriority(IssuePriority.LOW);
        issue.setProject(savedProject);
        issue.setCreatedBy(user);
        issue.setCreatedAt(Instant.now());
        Issue savedIssue = issueRepository.save(issue);
        Comment comment = new Comment();
        comment.setMessage("This is a test comment.");
        comment.setIssue(savedIssue);
        comment.setUser(user);
        commentRepository.save(comment);

        Page<Comment> result = commentRepository.findByIssue_IssueId(savedIssue.getIssueId(),
                PageRequest.of(0, 10));

        assertEquals(result.getTotalElements(), 1);
        assertEquals(result.getContent().get(0).getMessage(), "This is a test comment.");
    }

    @Test
    void findByIssue_IssueIdNoCommentsExistTest() {
        UUID issueWithNoCommentsId = UUID.randomUUID();

        Page<Comment> result = commentRepository.findByIssue_IssueId(issueWithNoCommentsId,
                PageRequest.of(0, 10));

        assertEquals(result.getTotalElements(), 0);
    }
}
