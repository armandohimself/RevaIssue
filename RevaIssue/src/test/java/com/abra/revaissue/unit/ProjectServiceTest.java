package com.abra.revaissue.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abra.revaissue.dto.project.UpdateProjectRequest;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.EntityType;
import com.abra.revaissue.enums.ProjectStatus;
import com.abra.revaissue.repository.ProjectRepository;
import com.abra.revaissue.service.AuthzService;
import com.abra.revaissue.service.LogTransactionService;
import com.abra.revaissue.service.ProjectService;
import com.abra.revaissue.service.UserService;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    // Mocks
    @Mock private ProjectRepository projectRepository;
    @Mock private AuthzService authzService;
    @Mock private LogTransactionService logTransactionService;
    @Mock private UserService userService;

    // System Under Test
    @InjectMocks private ProjectService projectService;

    // Helpers
    private static Project project(String name, String desc) {
        Project project = new Project();
        project.setProjectName(name);
        project.setProjectDescription(desc);
        return project;
    }

    private static User user(String userName) {
        User user = new User();
        user.setUserName(userName);
        return user;
    }

    //! CREATE

    @Test
    void create_throws_if_project_null() {
        UUID actingUserId = UUID.randomUUID();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectService.create(null, actingUserId));

        assertTrue(exception.getMessage().toLowerCase().contains("cannot be null"));
        // No stubs needed here; it fails before any collaborator call.
    }

    @Test
    void create_throws_if_name_blank() {
        UUID actingUserId = UUID.randomUUID();
        Project p = project("   ", "desc");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> projectService.create(p, actingUserId));

        assertTrue(ex.getMessage().toLowerCase().contains("name"));
    }

    @Test
    void create_throws_if_acting_user_id_null() {
        Project p = project("Good Name", "desc");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> projectService.create(p, null));

        assertTrue(ex.getMessage().toLowerCase().contains("actinguserid"));
    }

    @Test
    void create_sets_defaults_saves_and_logs() {
        UUID actingUserId = UUID.randomUUID();
        Project p = project("Project A", "Created in unit test");

        // Only stub what THIS test reaches/uses
        when(userService.getUserByUUID(actingUserId)).thenReturn(user("admin"));

        // Save should return a Project; we’ll “simulate” DB generating an ID
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project saved = inv.getArgument(0);
            if (saved.getProjectId() == null) saved.setProjectId(UUID.randomUUID());
            return saved;
        });

        Project created = projectService.create(p, actingUserId);

        assertNotNull(created.getProjectId(), "projectId should be set after save()");
        assertEquals("Project A", created.getProjectName());
        assertEquals("Created in unit test", created.getProjectDescription());

        // Defaults your service enforces
        assertEquals(ProjectStatus.ACTIVE, created.getProjectStatus(), "default status should be ACTIVE");
        assertEquals(actingUserId, created.getCreatedByUserId(), "createdByUserId should be acting user");
        assertNotNull(created.getCreatedAt(), "createdAt should be set");
        assertNotNull(created.getUpdatedAt(), "updatedAt should be set");

        // Verify “guard + save + log” happened
        verify(authzService).mustBeAdmin(actingUserId);
        verify(projectRepository).save(any(Project.class));
        verify(logTransactionService).logAction(
            contains("created"),
            any(User.class),
            eq(EntityType.PROJECT),
            eq(created.getProjectId())
        );
    }

    //! READ

    @Test
    void getAll_delegates_to_repository_findAll() {
        when(projectRepository.findAll()).thenReturn(List.of());

        List<Project> res = projectService.getAll();

        assertNotNull(res);
        verify(projectRepository).findAll();
    }

    @Test
    void getById_throws_if_missing() {
        UUID projectId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> projectService.getById(projectId));

        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }

    @Test
    void getById_returns_project_if_found() {
        UUID projectId = UUID.randomUUID();
        Project existing = project("Found", "desc");
        existing.setProjectId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));

        Project res = projectService.getById(projectId);

        assertEquals(projectId, res.getProjectId());
        assertEquals("Found", res.getProjectName());
    }

    //! UPDATE

    @Test
    void update_throws_if_project_id_null() {
        UUID actingUserId = UUID.randomUUID();
        UpdateProjectRequest req = new UpdateProjectRequest("x", "y", ProjectStatus.ACTIVE);

        assertThrows(IllegalArgumentException.class,
            () -> projectService.update(null, req, actingUserId));
    }

    @Test
    void update_throws_if_request_null() {
        UUID actingUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
            () -> projectService.update(projectId, null, actingUserId));
    }

    @Test
    void update_throws_if_acting_user_null() {
        UUID projectId = UUID.randomUUID();
        UpdateProjectRequest req = new UpdateProjectRequest("x", "y", ProjectStatus.ACTIVE);

        assertThrows(IllegalArgumentException.class,
            () -> projectService.update(projectId, req, null));
    }

    @Test
    void update_throws_if_project_name_present_but_blank() {
        UUID actingUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project existing = project("Before", "desc");
        existing.setProjectId(projectId);
        existing.setProjectStatus(ProjectStatus.ACTIVE);
        existing.setCreatedByUserId(UUID.randomUUID());
        existing.setCreatedAt(Instant.now());
        existing.setUpdatedAt(Instant.now());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));

        UpdateProjectRequest req = new UpdateProjectRequest("   ", "after", ProjectStatus.ACTIVE);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> projectService.update(projectId, req, actingUserId));

        assertTrue(ex.getMessage().toLowerCase().contains("cannot be blank"));
    }

    @Test
    void update_patches_fields_and_archives_when_status_archived() {
        UUID actingUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project existing = project("Before", "before");
        existing.setProjectId(projectId);
        existing.setProjectStatus(ProjectStatus.ACTIVE);
        existing.setCreatedByUserId(UUID.randomUUID());
        existing.setCreatedAt(Instant.now());
        existing.setUpdatedAt(Instant.now());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(userService.getUserByUUID(actingUserId)).thenReturn(user("admin"));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateProjectRequest req = new UpdateProjectRequest("After", "after", ProjectStatus.ARCHIVED);

        Project updated = projectService.update(projectId, req, actingUserId);

        assertEquals("After", updated.getProjectName());
        assertEquals("after", updated.getProjectDescription());
        assertEquals(ProjectStatus.ARCHIVED, updated.getProjectStatus());

        // Status transition audit fields
        assertEquals(actingUserId, updated.getStatusUpdatedByUserId());
        assertEquals(actingUserId, updated.getArchivedByUserId());
        assertNotNull(updated.getArchivedAt());
        assertNotNull(updated.getUpdatedAt());

        verify(authzService).mustBeAdmin(actingUserId);
        verify(projectRepository).save(any(Project.class));
        verify(logTransactionService).logAction(
            contains("updated"),
            any(User.class),
            eq(EntityType.PROJECT),
            eq(projectId)
        );
    }

    @Test
    void update_unarchives_when_status_active() {
        UUID actingUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project existing = project("Was Archived", "desc");
        existing.setProjectId(projectId);
        existing.setProjectStatus(ProjectStatus.ARCHIVED);
        existing.setArchivedByUserId(UUID.randomUUID());
        existing.setArchivedAt(Instant.now());
        existing.setCreatedByUserId(UUID.randomUUID());
        existing.setCreatedAt(Instant.now());
        existing.setUpdatedAt(Instant.now());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(userService.getUserByUUID(actingUserId)).thenReturn(user("admin"));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateProjectRequest req = new UpdateProjectRequest(null, null, ProjectStatus.ACTIVE);

        Project updated = projectService.update(projectId, req, actingUserId);

        assertEquals(ProjectStatus.ACTIVE, updated.getProjectStatus());
        assertNull(updated.getArchivedByUserId(), "unarchive should clear archivedByUserId");
        assertNull(updated.getArchivedAt(), "unarchive should clear archivedAt");
        assertEquals(actingUserId, updated.getStatusUpdatedByUserId(), "statusUpdatedByUserId should be acting user");
    }

    //! ARCHIVE (DELETE behavior)

    @Test
    void archive_sets_archived_fields_and_saves() {
        UUID actingUserId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project existing = project("To Archive", "desc");
        existing.setProjectId(projectId);
        existing.setProjectStatus(ProjectStatus.ACTIVE);
        existing.setCreatedByUserId(UUID.randomUUID());
        existing.setCreatedAt(Instant.now());
        existing.setUpdatedAt(Instant.now());

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        projectService.archive(projectId, actingUserId);

        // Capture the saved project and assert what got changed
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project saved = captor.getValue();

        assertEquals(ProjectStatus.ARCHIVED, saved.getProjectStatus());
        assertEquals(actingUserId, saved.getArchivedByUserId());
        assertNotNull(saved.getArchivedAt());
        assertEquals(actingUserId, saved.getStatusUpdatedByUserId());
        assertNotNull(saved.getUpdatedAt());

        verify(authzService).mustBeAdmin(actingUserId);
    }
}