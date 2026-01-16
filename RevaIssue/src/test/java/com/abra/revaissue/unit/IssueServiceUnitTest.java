package com.abra.revaissue.unit;

import com.abra.revaissue.dto.IssueCreateDTO;
import com.abra.revaissue.dto.IssueResponseDTO;
import com.abra.revaissue.entity.Issue;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.*;
import com.abra.revaissue.repository.IssueRepository;
import com.abra.revaissue.service.IssueService;
import com.abra.revaissue.service.LogTransactionService;
import com.abra.revaissue.service.UserService;
import com.abra.revaissue.util.IssueMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IssueServiceUnitTest {
    @Mock
    private IssueRepository issueRepository;
    @Mock
    private IssueMapper issueMapper;
    @Mock
    private UserService userService;
    @Mock
    private LogTransactionService logTransactionService;
    @InjectMocks
    private IssueService issueService;

    private User tester;
    private User admin;
    private User dev;
    private User otherDev;
    private Project project;

    @BeforeEach
    void setup() {
        tester = new User();
        tester.setUserId(UUID.randomUUID());
        tester.setUserName("tester1");
        tester.setRole(UserEnum.Role.TESTER);

        admin = new User();
        admin.setUserId(UUID.randomUUID());
        admin.setUserName("admin1");
        admin.setRole(UserEnum.Role.ADMIN);

        dev = new User();
        dev.setUserId(UUID.randomUUID());
        dev.setUserName("dev1");
        dev.setRole(UserEnum.Role.DEVELOPER);

        otherDev = new User();
        otherDev.setUserId(UUID.randomUUID());
        otherDev.setUserName("dev2");
        otherDev.setRole(UserEnum.Role.DEVELOPER);

        project = new Project();
        project.setProjectId(UUID.randomUUID());
        project.setProjectName("project1");

    }

    private Issue makeIssue(UUID id,
                            String name,
                            Project project,
                            User createdBy,
                            User assignedTo,
                            IssueStatus status,
                            IssueSeverity severity,
                            IssuePriority priority){
        Issue issue = new Issue();
        issue.setIssueId(id);
        issue.setName(name);
        issue.setProject(project);
        issue.setCreatedBy(createdBy);
        issue.setAssignedTo(assignedTo);
        issue.setStatus(status);
        issue.setSeverity(severity);
        issue.setPriority(priority);
        issue.setCreatedAt(Instant.now());
        issue.setUpdatedAt(Instant.now());
        return issue;
    }

    @Test
    void createIssuePositiveTest(){
        IssueCreateDTO dto = new IssueCreateDTO();
        Issue mapped = new Issue();
        mapped.setName("Issue 1");
        Issue saved = new Issue();
        saved.setIssueId(UUID.randomUUID());
        saved.setName("Issue 1");

        IssueResponseDTO response = new IssueResponseDTO();
        when(issueMapper.toIssue(dto)).thenReturn(mapped);
        when(issueRepository.save(any(Issue.class))).thenReturn(saved);
        when(issueMapper.toResponseDTO(saved)).thenReturn(response);

        IssueResponseDTO result = issueService.createIssue(dto, project, tester);
        assertNotNull(result);
        verify(issueMapper).toIssue(dto);
        verify(issueRepository).save(argThat(i ->
                i.getProject() == project &&
                        i.getCreatedBy() == tester &&
                        i.getStatus() == IssueStatus.OPEN &&
                        i.getCreatedAt() != null &&
                        i.getUpdatedAt() != null
        ));
        verify(issueMapper).toResponseDTO(saved);

    }
}
