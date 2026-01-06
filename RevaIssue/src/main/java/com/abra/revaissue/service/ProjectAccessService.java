package com.abra.revaissue.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.ProjectAccess;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.ProjectRole;
import com.abra.revaissue.enums.ProjectStatus;
import com.abra.revaissue.enums.UserEnum;
import com.abra.revaissue.exception.UnauthorizedOperation;
import com.abra.revaissue.repository.ProjectAccessRepository;
import com.abra.revaissue.repository.ProjectRepository;

import org.springframework.stereotype.Service;

@Service
public class ProjectAccessService {

    private final ProjectAccessRepository projectAccessRepository;
    private final ProjectRepository projectRepository;
    private final AuthzService authzService;
    private final UserService userService;

    public ProjectAccessService(
        ProjectAccessRepository projectAccessRepository,
        ProjectRepository projectRepository,
        AuthzService authzService,
        UserService userService
    ) {
        this.projectAccessRepository = projectAccessRepository;
        this.projectRepository = projectRepository;
        this.authzService = authzService;
        this.userService = userService;
    }

    //! CREATE
    public ProjectAccess assignAccess(ProjectAccess projectAccess, UUID adminUserId) {
        // Guard rails
        if (projectAccess == null)
            throw new IllegalArgumentException("Project Access cannot be null!");

        if (adminUserId == null)
            throw new IllegalArgumentException("Missing required fields!");

        if (projectAccess.getProjectId() == null)
            throw new IllegalArgumentException("Missing required fields!");

        if (projectAccess.getUserId() == null)
            throw new IllegalArgumentException("Missing required fields!");

        if (projectAccess.getProjectRole() == null)
            throw new IllegalArgumentException("Missing required fields!");

        // Admin-only
        authzService.mustBeAdmin(adminUserId);

        UUID projectId = projectAccess.getProjectId();
        UUID userId = projectAccess.getUserId();
        ProjectRole projectRole = projectAccess.getProjectRole();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found!"));

        // If project is archived, you shouldn't be able to assign someone to it.
        if (project.getProjectStatus() == ProjectStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot grant access to an archived project!");
        }

        // Prevent duplicate active access
        if (projectAccessRepository.existsByProjectIdAndUserIdAndRevokedAccessAtIsNull(projectId, userId)) {
            throw new IllegalStateException("User already has active access to this project!");
        }

        Instant now = Instant.now();

        ProjectAccess toSave = new ProjectAccess();
        toSave.setProjectId(projectId);
        toSave.setUserId(userId);
        toSave.setProjectRole(projectRole);
        toSave.setAssignedByUserId(adminUserId);
        toSave.setAccessAssignedAt(now);
        toSave.setRemovedByUserId(null);
        toSave.setRevokedAccessAt(null);

        return projectAccessRepository.save(toSave);
    }

    //! READ
    public List<Project> listActiveProjectsForUser(UUID userId) {

        /**
         * Get ALL ACTIVE project member records for this user (not revoked)
         * These rows contain which projects the user belongs to.
         */
        List<ProjectAccess> accesses = projectAccessRepository.findByUserIdAndRevokedAccessAtIsNull(userId);

        /**
         * Now pull just the projectId values out of those rows using distinct(),
         * avoids duplicates if anything weird happened in our data.
         */
        List<UUID> projectIds = accesses.stream()
            .map(ProjectAccess::getProjectId)
            .distinct()
            .toList();

        /**
         * If user isn't on any projects, return an empty list early
         */
        if (projectIds.isEmpty())
            return List.of();

        /**
         * Otherwise fetch the actual Project entities in one query:
         * Equivalent SQL idea: SELECT * FROM projects WHERE project_id IN ( ... );
         */
        return projectRepository.findByProjectIdIn(projectIds);
    }

    // show me a list of members active on a specific project
    public List<ProjectAccess> findActiveAccessByProjectId(UUID projectId, UUID adminUserId) {

        // * Admin-only
        authzService.mustBeAdmin(adminUserId);

        return projectAccessRepository.findByProjectIdAndRevokedAccessAtIsNull(projectId);
    }

    // show me a list of members active on a specific project tester allowed
    public List<User> findMembersByProjectId(UUID projectId, UUID actingUserId) {
        User actingUser = userService.getUserByUUID(actingUserId);
        List<User> memberList = new ArrayList<>();
        if(actingUser.getRole() != UserEnum.Role.ADMIN && projectAccessRepository.findByProjectIdAndUserIdAndRevokedAccessAtIsNull(projectId, actingUserId) == null){
            throw new UnauthorizedOperation("Only ADMINS or members can view project members");
        }
        List<ProjectAccess> accessList = projectAccessRepository.findByProjectIdAndRevokedAccessAtIsNull(projectId);
        for(ProjectAccess access: accessList){
            User user = userService.getUserByUUID(access.getUserId());
            memberList.add(user);
        }
        return memberList;
    }

    // show me a user in a project which may/not exist
    public Optional<ProjectAccess> findActiveAccessByProjectIdAndUserId(UUID projectId, UUID userId) {
        return projectAccessRepository.findByProjectIdAndUserIdAndRevokedAccessAtIsNull(projectId, userId);
    }

    // show me all the testers/devs/(maybe admins) for a specific project
    public List<ProjectAccess> listActiveAccessByProjectIdAndRole(UUID projectId, ProjectRole projectRole) {
        return projectAccessRepository.findByProjectIdAndProjectRoleAndRevokedAccessAtIsNull(projectId, projectRole);
    }

    //! DELETE
    public ProjectAccess revokeAccess(UUID projectId, UUID userIdToRevoke, UUID removedByUserId) {

        if (projectId == null || userIdToRevoke == null || removedByUserId == null) {
            throw new IllegalArgumentException("Missing required fields!");
        }

        // * Admin-only
        authzService.mustBeAdmin(removedByUserId);

        ProjectAccess projectAccess = this.findActiveAccessByProjectIdAndUserId(projectId, userIdToRevoke)
                .orElseThrow(() -> new IllegalArgumentException("Active project access was not found!"));

        /**
         * TODO: Add this record change to Log Transaction
         */
        projectAccess.setRemovedByUserId(removedByUserId);
        projectAccess.setRevokedAccessAt(Instant.now());

        return projectAccessRepository.save(projectAccess);
    }
}
