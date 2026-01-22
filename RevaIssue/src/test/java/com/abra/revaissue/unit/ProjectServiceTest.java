package com.abra.revaissue.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abra.revaissue.dto.project.UpdateProjectRequest;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.*;
import com.abra.revaissue.repository.ProjectRepository;
import com.abra.revaissue.service.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository repo;
    @Mock private AuthzService authz;
    @Mock private LogTransactionService logger;
    @Mock private UserService userService;
    @InjectMocks private ProjectService service;
    
    private UUID adminId;
    private User admin;
    
    @BeforeEach
    void setup() {
        adminId = UUID.randomUUID();
        admin = new User();
        admin.setUserName("admin");
    }

    @Test
    void create_saves_project_with_defaults() {
        Project p = new Project();
        p.setProjectName("New API");
        p.setProjectDescription("REST API");

        when(userService.getUserByUUID(adminId)).thenReturn(admin);
        when(repo.save(any(Project.class))).thenAnswer(inv -> {
            Project saved = inv.getArgument(0);
            saved.setProjectId(UUID.randomUUID());
            return saved;
        });

        Project result = service.create(p, adminId);

        assertNotNull(result.getProjectId());
        assertEquals("New API", result.getProjectName());
        assertEquals(ProjectStatus.ACTIVE, result.getProjectStatus());
        assertEquals(adminId, result.getCreatedByUserId());
        assertNotNull(result.getCreatedAt());
        verify(authz).mustBeAdmin(adminId);
        verify(logger).logAction(anyString(), any(), eq(EntityType.PROJECT), any());
    }
    
    @Test
    void create_rejects_blank_name() {
        Project p = new Project();
        p.setProjectName("  ");

        assertThrows(IllegalArgumentException.class, 
            () -> service.create(p, adminId));
    }
    
    @Test
    void create_rejects_null_input() {
        assertThrows(IllegalArgumentException.class, 
            () -> service.create(null, adminId));
    }

    @Test
    void getById_returns_project() {
        UUID id = UUID.randomUUID();
        Project p = new Project();
        p.setProjectId(id);
        p.setProjectName("Found");
        
        when(repo.findById(id)).thenReturn(Optional.of(p));

        Project result = service.getById(id);

        assertEquals("Found", result.getProjectName());
    }
    
    @Test
    void getById_throws_when_missing() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> service.getById(id));
    }

    @Test
    void update_patches_name_and_description() {
        UUID id = UUID.randomUUID();
        Project existing = new Project();
        existing.setProjectId(id);
        existing.setProjectName("Old");
        existing.setProjectStatus(ProjectStatus.ACTIVE);
        
        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(userService.getUserByUUID(adminId)).thenReturn(admin);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        UpdateProjectRequest req = new UpdateProjectRequest("New", "Updated", null);

        Project result = service.update(id, req, adminId);

        assertEquals("New", result.getProjectName());
        assertEquals("Updated", result.getProjectDescription());
        verify(authz).mustBeAdmin(adminId);
    }
    
    @Test
    void update_archives_project() {
        UUID id = UUID.randomUUID();
        Project active = new Project();
        active.setProjectId(id);
        active.setProjectStatus(ProjectStatus.ACTIVE);
        
        when(repo.findById(id)).thenReturn(Optional.of(active));
        when(userService.getUserByUUID(adminId)).thenReturn(admin);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        UpdateProjectRequest req = new UpdateProjectRequest(null, null, ProjectStatus.ARCHIVED);

        Project result = service.update(id, req, adminId);

        assertEquals(ProjectStatus.ARCHIVED, result.getProjectStatus());
        assertEquals(adminId, result.getArchivedByUserId());
        assertNotNull(result.getArchivedAt());
    }
    
    @Test
    void update_unarchives_project() {
        UUID id = UUID.randomUUID();
        Project archived = new Project();
        archived.setProjectId(id);
        archived.setProjectStatus(ProjectStatus.ARCHIVED);
        archived.setArchivedByUserId(UUID.randomUUID());
        
        when(repo.findById(id)).thenReturn(Optional.of(archived));
        when(userService.getUserByUUID(adminId)).thenReturn(admin);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        UpdateProjectRequest req = new UpdateProjectRequest(null, null, ProjectStatus.ACTIVE);

        Project result = service.update(id, req, adminId);

        assertEquals(ProjectStatus.ACTIVE, result.getProjectStatus());
        assertNull(result.getArchivedByUserId());
        assertNull(result.getArchivedAt());
    }

    @Test
    void archive_marks_project_archived() {
        UUID id = UUID.randomUUID();
        Project active = new Project();
        active.setProjectId(id);
        active.setProjectStatus(ProjectStatus.ACTIVE);
        
        when(repo.findById(id)).thenReturn(Optional.of(active));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.archive(id, adminId);

        verify(repo).save(argThat(p -> 
            p.getProjectStatus() == ProjectStatus.ARCHIVED &&
            p.getArchivedByUserId().equals(adminId)
        ));
        verify(authz).mustBeAdmin(adminId);
    }
}
