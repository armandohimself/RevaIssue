package com.abra.revaissue.unit;

import com.abra.revaissue.dto.IssueCreateDTO;
import com.abra.revaissue.dto.IssueResponseDTO;
import com.abra.revaissue.dto.IssueUpdateDTO;
import com.abra.revaissue.entity.Issue;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.*;
import com.abra.revaissue.exception.UnauthorizedOperation;
import com.abra.revaissue.repository.IssueRepository;
import com.abra.revaissue.service.IssueService;
import com.abra.revaissue.service.LogTransactionService;
import com.abra.revaissue.service.UserService;
import com.abra.revaissue.util.IssueMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
    void createIssuePositiveTestTester(){
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
        assertEquals(response, result);
        ArgumentCaptor<Issue> captor = ArgumentCaptor.forClass(Issue.class);
        verify(issueRepository).save(captor.capture());
        Issue issuePassedToSave = captor.getValue();

        assertEquals("Issue 1", issuePassedToSave.getName());
        assertEquals(project, issuePassedToSave.getProject());
        assertEquals(tester, issuePassedToSave.getCreatedBy());
        assertEquals(IssueStatus.OPEN, issuePassedToSave.getStatus());
        assertNotNull(issuePassedToSave.getCreatedAt());
        assertNotNull(issuePassedToSave.getUpdatedAt());
    }

    @Test
    void createIssueNegativeTestDeveloper(){
        IssueCreateDTO dto = new IssueCreateDTO();
        assertThrows(UnauthorizedOperation.class, () -> issueService.createIssue(dto, project, dev));
    }

    @Test
    void createIssueNegativeTestAdmin(){
        IssueCreateDTO dto = new IssueCreateDTO();
        assertThrows(UnauthorizedOperation.class, () -> issueService.createIssue(dto, project, admin));
    }

    @Test
    void getIssuesByProjectPositiveTest() {
        Issue issue1 = makeIssue(UUID.randomUUID(), "I1", project, tester, dev, IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);
        Issue issue2 = makeIssue(UUID.randomUUID(), "I2", project, tester, dev, IssueStatus.RESOLVED, IssueSeverity.HIGH, IssuePriority.MEDIUM);

        when(issueRepository.findByProject_ProjectId(project.getProjectId())).thenReturn(List.of(issue1, issue2));

        IssueResponseDTO dto1 = new IssueResponseDTO();
        IssueResponseDTO dto2 = new IssueResponseDTO();
        when(issueMapper.toResponseDTO(issue1)).thenReturn(dto1);
        when(issueMapper.toResponseDTO(issue2)).thenReturn(dto2);

        List<IssueResponseDTO> results = issueService.getIssuesByProject(project);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(dto1, results.get(0));
        assertEquals(dto2, results.get(1));

        verify(issueRepository).findByProject_ProjectId(project.getProjectId());
        verify(issueMapper).toResponseDTO(issue1);
        verify(issueMapper).toResponseDTO(issue2);
    }

    @Test
    void getIssuesByProjectNegativeTest(){
        when(issueRepository.findByProject_ProjectId(project.getProjectId())).thenReturn(List.of());

        List<IssueResponseDTO> results = issueService.getIssuesByProject(project);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(issueRepository).findByProject_ProjectId(project.getProjectId());
    }

    @Test
    void getIssuesByProjectStatusPositiveTest() {
        Issue issue1 = makeIssue(UUID.randomUUID(), "I1", project, tester, dev, IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);

        when(issueRepository.findByProject_ProjectIdAndStatus(project.getProjectId(), IssueStatus.OPEN)).thenReturn(List.of(issue1));

        IssueResponseDTO dto1 = new IssueResponseDTO();
        when(issueMapper.toResponseDTO(issue1)).thenReturn(dto1);

        List<IssueResponseDTO> results = issueService.getIssuesByProjectAndStatus(project, IssueStatus.OPEN);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dto1, results.get(0));

        verify(issueRepository).findByProject_ProjectIdAndStatus(project.getProjectId(), IssueStatus.OPEN);
        verify(issueMapper).toResponseDTO(issue1);
    }

    @Test
    void getIssuesByProjectStatusNegativeTest(){
        when(issueRepository.findByProject_ProjectIdAndStatus(project.getProjectId(), IssueStatus.CLOSED)).thenReturn(List.of());

        List<IssueResponseDTO> results = issueService.getIssuesByProjectAndStatus(project, IssueStatus.CLOSED);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(issueRepository).findByProject_ProjectIdAndStatus(project.getProjectId(), IssueStatus.CLOSED);
    }

    @Test
    void getIssuesByProjectSeverityPositiveTest() {
        Issue issue1 = makeIssue(UUID.randomUUID(), "I1", project, tester, dev,
                IssueStatus.OPEN, IssueSeverity.HIGH, IssuePriority.LOW);

        when(issueRepository.findByProject_ProjectIdAndSeverity(project.getProjectId(), IssueSeverity.HIGH))
                .thenReturn(List.of(issue1));

        IssueResponseDTO dto1 = new IssueResponseDTO();
        when(issueMapper.toResponseDTO(issue1)).thenReturn(dto1);

        List<IssueResponseDTO> results = issueService.getIssuesByProjectAndSeverity(project, IssueSeverity.HIGH);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dto1, results.get(0));

        verify(issueRepository).findByProject_ProjectIdAndSeverity(project.getProjectId(), IssueSeverity.HIGH);
        verify(issueMapper).toResponseDTO(issue1);
    }

    @Test
    void getIssuesByProjectSeverityNegativeTest(){
        when(issueRepository.findByProject_ProjectIdAndSeverity(project.getProjectId(), IssueSeverity.HIGH)).thenReturn(List.of());

        List<IssueResponseDTO> results = issueService.getIssuesByProjectAndSeverity(project, IssueSeverity.HIGH);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(issueRepository).findByProject_ProjectIdAndSeverity(project.getProjectId(), IssueSeverity.HIGH);
    }

    @Test
    void getIssuesByProjectPriorityPositiveTest() {
        Issue issue1 = makeIssue(UUID.randomUUID(), "I1", project, tester, dev, IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.HIGH);

        when(issueRepository.findByProject_ProjectIdAndPriority(project.getProjectId(), IssuePriority.HIGH)).thenReturn(List.of(issue1));

        IssueResponseDTO dto1 = new IssueResponseDTO();
        when(issueMapper.toResponseDTO(issue1)).thenReturn(dto1);

        List<IssueResponseDTO> results = issueService.getIssuesByProjectAndPriority(project, IssuePriority.HIGH);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dto1, results.get(0));

        verify(issueRepository).findByProject_ProjectIdAndPriority(project.getProjectId(), IssuePriority.HIGH);
        verify(issueMapper).toResponseDTO(issue1);
    }

    @Test
    void getIssuesByProjectPriorityNegativeTest(){
        when(issueRepository.findByProject_ProjectIdAndPriority(project.getProjectId(), IssuePriority.HIGH)).thenReturn(List.of());

        List<IssueResponseDTO> results = issueService.getIssuesByProjectAndPriority(project, IssuePriority.HIGH);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(issueRepository).findByProject_ProjectIdAndPriority(project.getProjectId(), IssuePriority.HIGH);
    }

    @Test
    void getIssuesCreatedByUserPositiveTest() {
        Issue issue1 = makeIssue(UUID.randomUUID(), "I1", project, tester, dev, IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);

        when(issueRepository.findByCreatedBy_UserId(tester.getUserId())).thenReturn(List.of(issue1));

        IssueResponseDTO dto1 = new IssueResponseDTO();
        when(issueMapper.toResponseDTO(issue1)).thenReturn(dto1);

        List<IssueResponseDTO> results = issueService.getIssuesCreatedByUser(tester);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dto1, results.get(0));

        verify(issueRepository).findByCreatedBy_UserId(tester.getUserId());
        verify(issueMapper).toResponseDTO(issue1);
    }

    @Test
    void getIssuesCreatedByUserNegativeTest(){
        when(issueRepository.findByCreatedBy_UserId(tester.getUserId())).thenReturn(List.of());

        List<IssueResponseDTO> results = issueService.getIssuesCreatedByUser(tester);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(issueRepository).findByCreatedBy_UserId(tester.getUserId());
    }

    @Test
    void getIssuesAssignedToUserPositiveTest() {
        Issue issue1 = makeIssue(UUID.randomUUID(), "I1", project, tester, dev,
                IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);

        when(issueRepository.findByAssignedTo_UserId(dev.getUserId()))
                .thenReturn(List.of(issue1));

        IssueResponseDTO dto1 = new IssueResponseDTO();
        when(issueMapper.toResponseDTO(issue1)).thenReturn(dto1);

        List<IssueResponseDTO> results = issueService.getIssuesAssignedToUser(dev);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dto1, results.get(0));

        verify(issueRepository).findByAssignedTo_UserId(dev.getUserId());
        verify(issueMapper).toResponseDTO(issue1);
    }

    @Test
    void getIssuesAssignedToUserNegativeTest(){
        when(issueRepository.findByAssignedTo_UserId(dev.getUserId())).thenReturn(List.of());

        List<IssueResponseDTO> results = issueService.getIssuesAssignedToUser(dev);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(issueRepository).findByAssignedTo_UserId(dev.getUserId());
    }

    @Test
    void getIssuesByProjectAndAssignedToUserPositiveTest() {
        Issue issue1 = makeIssue(UUID.randomUUID(), "I1", project, tester, dev,
                IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);

        when(issueRepository.findByProject_ProjectIdAndAssignedTo_UserId(project.getProjectId(), dev.getUserId()))
                .thenReturn(List.of(issue1));

        IssueResponseDTO dto1 = new IssueResponseDTO();
        when(issueMapper.toResponseDTO(issue1)).thenReturn(dto1);

        List<IssueResponseDTO> results = issueService.getIssuesByProjectAndAssignedToUser(project, dev);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dto1, results.get(0));

        verify(issueRepository).findByProject_ProjectIdAndAssignedTo_UserId(project.getProjectId(), dev.getUserId());
        verify(issueMapper).toResponseDTO(issue1);

    }

    @Test
    void getIssuesByProjectAndAssignedToUserNegativeTest(){
        when(issueRepository.findByProject_ProjectIdAndAssignedTo_UserId(project.getProjectId(), dev.getUserId())).thenReturn(List.of());

        List<IssueResponseDTO> results = issueService.getIssuesByProjectAndAssignedToUser(project, dev);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(issueRepository).findByProject_ProjectIdAndAssignedTo_UserId(project.getProjectId(), dev.getUserId());
    }

    @Test
    void getIssuesByProjectAndCreatedByUserPositiveTest() {
        Issue issue1 = makeIssue(UUID.randomUUID(), "I1", project, tester, dev,
                IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);

        when(issueRepository.findByProject_ProjectIdAndCreatedBy_UserId(project.getProjectId(), tester.getUserId()))
                .thenReturn(List.of(issue1));

        IssueResponseDTO dto1 = new IssueResponseDTO();
        when(issueMapper.toResponseDTO(issue1)).thenReturn(dto1);

        List<IssueResponseDTO> results = issueService.getIssuesByProjectAndCreatedByUser(project, tester);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(dto1, results.get(0));

        verify(issueRepository).findByProject_ProjectIdAndCreatedBy_UserId(project.getProjectId(), tester.getUserId());
        verify(issueMapper).toResponseDTO(issue1);

    }

    @Test
    void getIssuesByProjectAndCreatedByUserNegativeTest(){
        when(issueRepository.findByProject_ProjectIdAndCreatedBy_UserId(project.getProjectId(), tester.getUserId())).thenReturn(List.of());
        List<IssueResponseDTO> results = issueService.getIssuesByProjectAndCreatedByUser(project, tester);

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(issueRepository).findByProject_ProjectIdAndCreatedBy_UserId(project.getProjectId(), tester.getUserId());
    }

    @Test
    void getIssueByIdPositiveTest() {
        UUID issueId = UUID.randomUUID();
        Issue issue = makeIssue(issueId, "I1", project, tester, dev, IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));

        IssueResponseDTO dto = new IssueResponseDTO();
        when(issueMapper.toResponseDTO(issue)).thenReturn(dto);

        IssueResponseDTO result = issueService.getIssueById(issueId);

        assertNotNull(result);
        assertEquals(dto, result);

        verify(issueRepository).findById(issueId);
        verify(issueMapper).toResponseDTO(issue);

    }

    @Test
    void getIssueByIdNegativeTest(){
        UUID issueId = UUID.randomUUID();
        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> issueService.getIssueById(issueId));
    }

    @Test
    void updateIssuePositiveTest(){
        UUID issueId = UUID.randomUUID();
        IssueUpdateDTO dto = new IssueUpdateDTO();

        Issue exists = makeIssue(issueId, "Issue 1", project, tester, dev,
                IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);
        IssueResponseDTO response = new IssueResponseDTO();

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(exists));
        when(issueRepository.save(any(Issue.class))).thenReturn(exists);
        when(issueMapper.toResponseDTO(exists)).thenReturn(response);

        IssueResponseDTO result = issueService.updateIssue(issueId, dto, tester);

        assertNotNull(result);
        assertEquals(response, result);

        verify(issueRepository).findById(issueId);
        verify(issueMapper).updateEntity(dto, exists);
        verify(issueRepository).save(exists);
        verify(logTransactionService).logAction(anyString(), eq(tester), eq(EntityType.ISSUE), eq(issueId));
        assertNotNull(exists.getUpdatedAt());
    }

    @Test
    void updateIssueNegativeTestNotTester(){
        UUID issueId = UUID.randomUUID();
        IssueUpdateDTO dto = new IssueUpdateDTO();
        assertThrows(UnauthorizedOperation.class,() -> issueService.updateIssue(issueId, dto, dev));
    }

    @Test
    void updateIssueNegativeTestIssueNotFound(){
        UUID issueId = UUID.randomUUID();
        IssueUpdateDTO dto = new IssueUpdateDTO();
        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,() -> issueService.updateIssue(issueId, dto, tester));
    }

    @Test
    void assignDeveloperPositiveTest(){
        UUID issueId = UUID.randomUUID();

        Issue existing = makeIssue(issueId, "Issue 1", project, tester, null,
                IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);

        IssueResponseDTO response = new IssueResponseDTO();

        when(userService.getUserByUUID(otherDev.getUserId())).thenReturn(otherDev);
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(existing));
        when(issueRepository.save(any(Issue.class))).thenReturn(existing);
        when(issueMapper.toResponseDTO(existing)).thenReturn(response);

        IssueResponseDTO result = issueService.assignDeveloper(issueId, otherDev.getUserId(), tester);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(otherDev, existing.getAssignedTo());

        verify(userService).getUserByUUID(otherDev.getUserId());
        verify(issueRepository).findById(issueId);
        verify(issueRepository).save(existing);
        verify(logTransactionService).logAction(anyString(), eq(tester), eq(EntityType.ISSUE), eq(issueId));
    }

    @Test
    void assignDeveloperNegativeTestNotTester(){
        UUID issueId = UUID.randomUUID();
        assertThrows(UnauthorizedOperation.class,() -> issueService.assignDeveloper(issueId, otherDev.getUserId(), dev));
    }

    @Test
    void assignDeveloperNegativeTestIssueNotFound(){
        UUID issueId = UUID.randomUUID();
        when(userService.getUserByUUID(otherDev.getUserId())).thenReturn(otherDev);
        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,() -> issueService.assignDeveloper(issueId, otherDev.getUserId(), tester));
    }

    @Test
    void deleteIssuePositiveTest(){
        UUID issueId = UUID.randomUUID();

        Issue existing = makeIssue(issueId, "Issue 1", project, tester, dev, IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(existing));

        issueService.deleteIssue(issueId, tester);

        verify(issueRepository).findById(issueId);
        verify(logTransactionService).logAction(anyString(), eq(tester), eq(EntityType.ISSUE), eq(issueId));
        verify(issueRepository).delete(existing);
    }

    @Test
    void deleteIssueNegativeTestNotTester(){
        UUID issueId = UUID.randomUUID();
        Issue existing = makeIssue(issueId, "Issue 1", project, tester, dev, IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(existing));
        assertThrows(UnauthorizedOperation.class, () -> issueService.deleteIssue(issueId, dev));
    }

    @Test
    void deleteIssueNegativeTestIssueNotFound(){
        UUID issueId = UUID.randomUUID();
        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> issueService.deleteIssue(issueId, tester));
    }

    @Test
    void updateStatusPositiveDeveloperMoveToResolved(){
        UUID issueId = UUID.randomUUID();

        Issue existing = makeIssue(issueId, "Issue 1", project, tester, dev,
                IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);

        IssueResponseDTO response = new IssueResponseDTO();

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(existing));
        when(issueRepository.save(any(Issue.class))).thenReturn(existing);
        when(issueMapper.toResponseDTO(existing)).thenReturn(response);

        IssueResponseDTO result = issueService.updateStatus(issueId, IssueStatus.RESOLVED, dev);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(IssueStatus.RESOLVED, existing.getStatus());

        verify(issueRepository).findById(issueId);
        verify(issueRepository).save(existing);
        verify(logTransactionService).logAction(anyString(), eq(dev), eq(EntityType.ISSUE), eq(issueId));
    }
    @Test
    void updateStatusNegativeDeveloperMoveToClosed(){
        UUID issueId = UUID.randomUUID();
        Issue existing = makeIssue(issueId, "Issue 1", project, tester, dev, IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(existing));
        assertThrows(UnauthorizedOperation.class, () -> issueService.updateStatus(issueId, IssueStatus.CLOSED, dev));
    }
    @Test
    void updateStatusPositiveTesterMoveToClosed(){
        UUID issueId = UUID.randomUUID();
        Issue existing = makeIssue(issueId, "Issue 1", project, tester, dev, IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);
        IssueResponseDTO response = new IssueResponseDTO();

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(existing));
        when(issueRepository.save(any(Issue.class))).thenReturn(existing);
        when(issueMapper.toResponseDTO(existing)).thenReturn(response);

        IssueResponseDTO result = issueService.updateStatus(issueId, IssueStatus.CLOSED, tester);

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(IssueStatus.CLOSED, existing.getStatus());

        verify(issueRepository).save(existing);
        verify(logTransactionService).logAction(anyString(), eq(tester), eq(EntityType.ISSUE), eq(issueId));
    }
    @Test
    void updateStatusNegativeTesterMoveToResolved(){
        UUID issueId = UUID.randomUUID();
        Issue existing = makeIssue(issueId, "Issue 1", project, tester, dev,
                IssueStatus.OPEN, IssueSeverity.LOW, IssuePriority.LOW);
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(existing));
        assertThrows(UnauthorizedOperation.class, () -> issueService.updateStatus(issueId, IssueStatus.RESOLVED, tester));
    }

}
