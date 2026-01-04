package com.abra.revaissue.util;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

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

import jakarta.annotation.PostConstruct;

@Component
public class DataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void preloadAdmin() {
        User admin = userRepository.findByUserName("admin");
        if (admin == null) {
            admin = new User();
            admin.setUserName("admin");
            admin.setPasswordHash(passwordEncoder.encode("password"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
        Project project = new Project();
        project.setProjectName("Default Project");
        project.setProjectDescription("This is a default project.");
        project.setCreatedAt(Instant.now());
        project.setUpdatedAt(Instant.now());
        project.setCreatedByUserId(admin.getUserId());
        Project savedProject = projectRepository.save(project);
        Issue issue = new Issue();
        issue.setName("Sample Issue");
        issue.setDescription("This is a sample issue for data loading.");
        issue.setStatus(IssueStatus.OPEN);
        issue.setSeverity(IssueSeverity.LOW);
        issue.setPriority(IssuePriority.LOW);
        issue.setProject(savedProject);
        issue.setCreatedBy(admin);
        issue.setCreatedAt(Instant.now());
        Issue savedIssue = issueRepository.save(issue);
        for (int i = 1; i <= 15; i++) {
            Comment comment = new Comment();
            comment.setMessage("Sample comment " + i);
            comment.setIssue(savedIssue);
            comment.setUser(admin);
            commentRepository.save(comment);
        }
        System.out.println("Issue ID: " + savedIssue.getIssueId().toString());
    }

}