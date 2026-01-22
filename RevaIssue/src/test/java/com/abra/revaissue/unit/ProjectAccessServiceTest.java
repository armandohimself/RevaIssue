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

import com.abra.revaissue.entity.*;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.*;
import com.abra.revaissue.exception.UnauthorizedOperation;
import com.abra.revaissue.repository.*;
import com.abra.revaissue.service.*;

@ExtendWith(MockitoExtension.class)
class ProjectAccessServiceTest {

    @Mock private ProjectAccessRepository accessRepo;
    @Mock private ProjectRepository projectRepo;
    @Mock private AuthzService authz;
    @Mock private UserService userService;
    @InjectMocks private ProjectAccessService service;
    
    private UUID adminId;
    private UUID projectId;
    private UUID userId;
    
    @BeforeEach
    void setup() {
        adminId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    // Happy path first - most important test
    @Test
    void assignAccess_works() {
        ProjectAccess request = new ProjectAccess();
        request.setProjectId(projectId);
        request.setUserId(userId);
        request.setProjectRole(ProjectRole.TESTER);

        Project activeProject = new Project();
        activeProject.setProjectStatus(ProjectStatus.ACTIVE);
        when(projectRepo.findById(projectId)).thenReturn(Optional.of(activeProject));
        when(accessRepo.existsByProjectIdAndUserIdAndRevokedAccessAtIsNull(projectId, userId))
            .thenReturn(false);
        when(accessRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProjectAccess result = service.assignAccess(request, adminId);

        assertNotNull(result);
        assertEquals(projectId, result.getProjectId());
        assertEquals(userId, result.getUserId());
        assertEquals(ProjectRole.TESTER, result.getProjectRole());
        assertEquals(adminId, result.getAssignedByUserId());
        assertNotNull(result.getAccessAssignedAt());
        verify(authz).mustBeAdmin(adminId);
    }
    
    @Test
    void assignAccess_rejects_null_inputs() {
        ProjectAccess valid = new ProjectAccess();
        valid.setProjectId(projectId);
        valid.setUserId(userId);
        valid.setProjectRole(ProjectRole.TESTER);
        
        assertThrows(IllegalArgumentException.class, 
            () -> service.assignAccess(null, adminId));
        assertThrows(IllegalArgumentException.class, 
            () -> service.assignAccess(valid, null));
    }
    
    @Test
    void assignAccess_rejects_archived_project() {
        ProjectAccess request = new ProjectAccess();
        request.setProjectId(projectId);
        request.setUserId(userId);
        request.setProjectRole(ProjectRole.TESTER);

        Project archived = new Project();
        archived.setProjectStatus(ProjectStatus.ARCHIVED);
        when(projectRepo.findById(projectId)).thenReturn(Optional.of(archived));

        assertThrows(IllegalStateException.class, 
            () -> service.assignAccess(request, adminId));
    }
    
    @Test
    void assignAccess_prevents_duplicates() {
        ProjectAccess request = new ProjectAccess();
        request.setProjectId(projectId);
        request.setUserId(userId);
        request.setProjectRole(ProjectRole.TESTER);

        Project active = new Project();
        active.setProjectStatus(ProjectStatus.ACTIVE);
        when(projectRepo.findById(projectId)).thenReturn(Optional.of(active));
        when(accessRepo.existsByProjectIdAndUserIdAndRevokedAccessAtIsNull(projectId, userId))
            .thenReturn(true);

        assertThrows(IllegalStateException.class, 
            () -> service.assignAccess(request, adminId));
        verify(accessRepo, never()).save(any());
    }

    @Test
    void listActiveProjects_empty_when_no_access() {
        when(accessRepo.findByUserIdAndRevokedAccessAtIsNull(userId))
            .thenReturn(Collections.emptyList());

        List<Project> projects = service.listActiveProjectsForUser(userId);

        assertTrue(projects.isEmpty());
    }
    
    @Test
    void listActiveProjects_fetches_user_projects() {
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        
        ProjectAccess a1 = new ProjectAccess();
        a1.setProjectId(p1);
        ProjectAccess a2 = new ProjectAccess();
        a2.setProjectId(p2);
        
        when(accessRepo.findByUserIdAndRevokedAccessAtIsNull(userId))
            .thenReturn(List.of(a1, a2));
        
        Project proj1 = new Project();
        proj1.setProjectId(p1);
        Project proj2 = new Project();
        proj2.setProjectId(p2);
        
        when(projectRepo.findByProjectIdIn(any())).thenReturn(List.of(proj1, proj2));

        List<Project> result = service.listActiveProjectsForUser(userId);

        assertEquals(2, result.size());
    }

    @Test
    void findMembers_blocks_non_members() {
        User tester = new User();
        tester.setRole(UserEnum.Role.TESTER);
        
        when(userService.getUserByUUID(userId)).thenReturn(tester);
        when(accessRepo.findByProjectIdAndUserIdAndRevokedAccessAtIsNull(projectId, userId))
            .thenReturn(Optional.empty());

        assertThrows(UnauthorizedOperation.class, 
            () -> service.findMembersByProjectId(projectId, userId));
    }
    
    @Test
    void findMembers_allows_admin_always() {
        User admin = new User();
        admin.setRole(UserEnum.Role.ADMIN);
        
        when(userService.getUserByUUID(adminId)).thenReturn(admin);
        when(accessRepo.findByProjectIdAndRevokedAccessAtIsNull(projectId))
            .thenReturn(Collections.emptyList());

        List<User> members = service.findMembersByProjectId(projectId, adminId);

        assertNotNull(members);
    }
    
    @Test
    void findMembers_allows_project_members() {
        User member = new User();
        member.setUserId(userId);
        member.setRole(UserEnum.Role.DEVELOPER);
        
        ProjectAccess access = new ProjectAccess();
        access.setUserId(userId);
        
        when(userService.getUserByUUID(userId)).thenReturn(member);
        when(accessRepo.findByProjectIdAndUserIdAndRevokedAccessAtIsNull(projectId, userId))
            .thenReturn(Optional.of(access));
        when(accessRepo.findByProjectIdAndRevokedAccessAtIsNull(projectId))
            .thenReturn(List.of(access));

        List<User> members = service.findMembersByProjectId(projectId, userId);

        assertEquals(1, members.size());
        assertEquals(userId, members.get(0).getUserId());
    }

    @Test
    void revokeAccess_marks_as_revoked() {
        ProjectAccess existing = new ProjectAccess();
        existing.setProjectId(projectId);
        existing.setUserId(userId);
        
        when(accessRepo.findByProjectIdAndUserIdAndRevokedAccessAtIsNull(projectId, userId))
            .thenReturn(Optional.of(existing));
        when(accessRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProjectAccess result = service.revokeAccess(projectId, userId, adminId);

        assertNotNull(result.getRevokedAccessAt());
        assertEquals(adminId, result.getRemovedByUserId());
        verify(authz).mustBeAdmin(eq(adminId));
    }
    
    @Test
    void revokeAccess_fails_if_not_active() {
        when(accessRepo.findByProjectIdAndUserIdAndRevokedAccessAtIsNull(projectId, userId))
            .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> service.revokeAccess(projectId, userId, adminId));
    }

}