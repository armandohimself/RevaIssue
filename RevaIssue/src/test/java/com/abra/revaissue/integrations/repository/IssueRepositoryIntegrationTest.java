package com.abra.revaissue.integrations.repository;

import com.abra.revaissue.entity.Issue;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import com.abra.revaissue.enums.IssueStatus;
import com.abra.revaissue.enums.UserEnum;
import com.abra.revaissue.repository.IssueRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@DataJpaTest
public class IssueRepositoryIntegrationTest {
    private IssueRepository issueRepository;
    private TestEntityManager manager;
    private User user1, user2;
    private Project project1, project2;
    private Issue issue1, issue2, issue3, issue4, issue5;

    @Autowired
    public IssueRepositoryIntegrationTest(IssueRepository issueRepository, TestEntityManager manager){
        this.issueRepository = issueRepository;
        this.manager = manager;
    }

    @BeforeEach
    void setUp(){
        user1 = new User();
        user1.setUserName("user1");
        user1.setPasswordHash("password");
        user1.setRole(UserEnum.Role.ADMIN);

        user2 = new User();
        user2.setUserName("user2");
        user2.setPasswordHash("password");
        user2.setRole(UserEnum.Role.TESTER);

        manager.persist(user1);
        manager.persist(user2);
        manager.flush();

        Instant time = Instant.now();

        project1 = new Project();
        project1.setProjectName("p1");
        project1.setCreatedByUserId(user1.getUserId());
        project1.setCreatedAt(time);
        project1.setUpdatedAt(time);

        project2 = new Project();
        project2.setProjectName("p2");
        project2.setCreatedByUserId(user2.getUserId());
        project2.setCreatedAt(time);
        project2.setUpdatedAt(time);

        manager.persist(project1);
        manager.persist(project2);
        manager.flush();

        issue1 = makeIssue("issue1", project1, user1, user2, IssueStatus.OPEN, IssueSeverity.HIGH, IssuePriority.HIGH);
        issue2 = makeIssue("issue2", project1, user2, user2, IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);
        issue3 = makeIssue("issue3", project1, user1, user1, IssueStatus.RESOLVED, IssueSeverity.MEDIUM, IssuePriority.HIGH);
        issue4 = makeIssue("issue4", project2, user1, user1, IssueStatus.OPEN, IssueSeverity.HIGH, IssuePriority.LOW);
        issue5 = makeIssue("issue5", project2, user2, null, IssueStatus.CLOSED, IssueSeverity.LOW, IssuePriority.MEDIUM);

        manager.persist(issue1);
        manager.persist(issue2);
        manager.persist(issue3);
        manager.persist(issue4);
        manager.persist(issue5);
        manager.flush();
        manager.clear();
    }

    private Issue makeIssue(String name,
                            Project project,
                            User createdBy,
                            User assignedTo,
                            IssueStatus status,
                            IssueSeverity severity,
                            IssuePriority priority){
        Issue issue = new Issue();
        issue.setName(name);
        issue.setProject(project);
        issue.setCreatedBy(createdBy);
        issue.setAssignedTo(assignedTo);
        issue.setStatus(status);
        issue.setSeverity(severity);
        issue.setPriority(priority);
        return issue;
    }

    private static List<String> issueNames(List<Issue> issues){
        return issues.stream().map(Issue::getName).toList();
    }

    @Test
    void findByProject_ProjectIdPositiveTest(){
        List<Issue> results = issueRepository.findByProject_ProjectId(project1.getProjectId());
        List<String> issueNames = issueNames(results);
        Assertions.assertEquals(3, results.size());
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getProject().getProjectId().equals(project1.getProjectId())));
        Assertions.assertTrue(issueNames.containsAll(List.of("issue1", "issue2", "issue3")));
    }

    @Test
    void findByProject_ProjectIdNegativeTest(){
        List<Issue> results = issueRepository.findByProject_ProjectId(UUID.randomUUID());
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void findByProject_ProjectIdAndStatusPositiveTest(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndStatus(project1.getProjectId(), IssueStatus.OPEN);
        List<String> issueNames = issueNames(results);
        Assertions.assertEquals(2, results.size());
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getProject().getProjectId().equals(project1.getProjectId())
                                                            && issue.getStatus() == IssueStatus.OPEN));
        Assertions.assertTrue(issueNames.containsAll(List.of("issue1", "issue2")));
    }

    @Test
    void findByProject_ProjectIdAndStatusNegativeTest(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndStatus(project1.getProjectId(), IssueStatus.CLOSED);
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void findByProject_ProjectIdAndStatusProject2(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndStatus(project2.getProjectId(), IssueStatus.OPEN);
        List<String> issueNames = issueNames(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getProject().getProjectId().equals(project2.getProjectId())
                && issue.getStatus() == IssueStatus.OPEN));
        Assertions.assertTrue(issueNames.containsAll(List.of("issue4")));
    }

    @Test
    void findByProject_ProjectIdAndSeverityPositiveTest(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndSeverity(project1.getProjectId(), IssueSeverity.HIGH);
        List<String> issueNames = issueNames(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getProject().getProjectId().equals(project1.getProjectId())
                && issue.getSeverity() == IssueSeverity.HIGH));
        Assertions.assertTrue(issueNames.containsAll(List.of("issue1")));
    }

    @Test
    void findByProject_ProjectIdAndSeverityProject2(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndSeverity(project2.getProjectId(), IssueSeverity.HIGH);
        List<String> issueNames = issueNames(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getProject().getProjectId().equals(project2.getProjectId())
                && issue.getSeverity() == IssueSeverity.HIGH));
        Assertions.assertTrue(issueNames.containsAll(List.of("issue4")));
    }

    @Test
    void findByProject_ProjectIdAndSeverityNegativeTest(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndSeverity(project2.getProjectId(), IssueSeverity.MEDIUM);
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void findByProject_ProjectIdAndPriorityPositiveTest(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndPriority(project1.getProjectId(), IssuePriority.HIGH);
        List<String> issueNames = issueNames(results);
        Assertions.assertEquals(2, results.size());
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getProject().getProjectId().equals(project1.getProjectId())
                && issue.getPriority() == IssuePriority.HIGH));
        Assertions.assertTrue(issueNames.containsAll(List.of("issue1", "issue3")));
    }

    @Test
    void findByProject_ProjectIdAndPriorityProject2(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndPriority(project2.getProjectId(), IssuePriority.LOW);
        List<String> issueNames = issueNames(results);
        Assertions.assertEquals(1, results.size());
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getProject().getProjectId().equals(project2.getProjectId())
                && issue.getPriority() == IssuePriority.LOW));
        Assertions.assertTrue(issueNames.containsAll(List.of("issue4")));
    }

    @Test
    void findByProject_ProjectIdAndPriorityNegativeTest(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndPriority(project1.getProjectId(), IssuePriority.MEDIUM);
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void findByCreatedBy_UserIdPositiveTest(){
        List<Issue> results = issueRepository.findByCreatedBy_UserId(user1.getUserId());
        Assertions.assertEquals(3, results.size());
        List<String> issueNames = issueNames(results);
        Assertions.assertTrue(issueNames.containsAll(List.of("issue1", "issue3", "issue4")));
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getCreatedBy().getUserId().equals(user1.getUserId())));
    }

    @Test
    void findByCreatedBy_UserIdNegativeTest(){
        List<Issue> results = issueRepository.findByCreatedBy_UserId(UUID.randomUUID());
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void findByAssignedTo_UserIdPositiveTest(){
        List<Issue> results = issueRepository.findByAssignedTo_UserId(user2.getUserId());
        Assertions.assertEquals(2, results.size());
        List<String> issueNames = issueNames(results);
        Assertions.assertTrue(issueNames.containsAll(List.of("issue1", "issue2")));
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getAssignedTo().getUserId().equals(user2.getUserId())));
    }

    @Test
    void findByAssignedTo_UserIdNegativeTest(){
        List<Issue> results = issueRepository.findByAssignedTo_UserId(UUID.randomUUID());
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void findByAssignedTo_UserIdNullableTest(){
        List<Issue> results = issueRepository.findByAssignedTo_UserId(user1.getUserId());
        Assertions.assertEquals(2, results.size());
        List<String> issueNames = issueNames(results);
        Assertions.assertTrue(issueNames.containsAll(List.of("issue3", "issue4")));
        Assertions.assertFalse(issueNames.contains("issue5"));
    }

    @Test
    void findByProject_ProjectIdAndAssignedTo_UserIdPositiveTest(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndAssignedTo_UserId(project1.getProjectId(), user2.getUserId());
        Assertions.assertEquals(2, results.size());
        List<String> issueNames = issueNames(results);
        Assertions.assertTrue(issueNames.containsAll(List.of("issue1", "issue2")));
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getAssignedTo().getUserId().equals(user2.getUserId())
                                                                    && issue.getProject().getProjectId().equals(project1.getProjectId())));
    }

    @Test
    void findByProject_ProjectIdAndAssignedTo_UserIdNegativeTest(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndAssignedTo_UserId(project2.getProjectId(), user2.getUserId());
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void findByProject_ProjectIdAndCreatedBy_UserIdPositiveTest(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndCreatedBy_UserId(project1.getProjectId(), user1.getUserId());
        Assertions.assertEquals(2, results.size());
        List<String> issueNames = issueNames(results);
        Assertions.assertTrue(issueNames.containsAll(List.of("issue1", "issue3")));
        Assertions.assertTrue(results.stream().allMatch(issue -> issue.getCreatedBy().getUserId().equals(user1.getUserId())
                && issue.getProject().getProjectId().equals(project1.getProjectId())));
    }

    @Test
    void findByProject_ProjectIdAndCreatedBy_UserIdNegativeTestUserId(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndCreatedBy_UserId(project1.getProjectId(), UUID.randomUUID());
        Assertions.assertTrue(results.isEmpty());
    }

    @Test
    void findByProject_ProjectIdAndCreatedBy_UserIdNegativeTestProjectId(){
        List<Issue> results = issueRepository.findByProject_ProjectIdAndCreatedBy_UserId(UUID.randomUUID(), user1.getUserId());
        Assertions.assertTrue(results.isEmpty());
    }
}
