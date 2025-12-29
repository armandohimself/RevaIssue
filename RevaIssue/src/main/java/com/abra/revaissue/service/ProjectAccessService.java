package com.abra.revaissue.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.ProjectAccess;
import com.abra.revaissue.enums.ProjectRole;
import com.abra.revaissue.enums.ProjectStatus;
import com.abra.revaissue.repository.ProjectAccessRepository;
import com.abra.revaissue.repository.ProjectRepository;
import com.abra.revaissue.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class ProjectAccessService {

    private final ProjectAccessRepository projectAccessRepository;
    private final ProjectRepository projectRepository;
    private final AuthzService authzService;

    public ProjectAccessService(ProjectAccessRepository projectAccessRepository, ProjectRepository projectRepository, AuthzService authzService) {
        this.projectAccessRepository = projectAccessRepository;
        this.projectRepository = projectRepository;
        this.authzService = authzService;
    }

    //! CREATE
    public ProjectAccess assignAccess(UUID projectId, UUID userId, ProjectRole projectRole, UUID assignedByUserId) {
        // Guard rails
        if (projectId == null || userId == null || projectRole == null || assignedByUserId == null) {
            throw new IllegalArgumentException("Missing required fields!");
        }

        // * Admin-only
        authzService.mustBeAdmin(assignedByUserId);

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

        ProjectAccess projectAccess = new ProjectAccess();
        projectAccess.setProjectId(projectId);
        projectAccess.setUserId(userId);
        projectAccess.setProjectRole(projectRole);
        projectAccess.setAssignedByUserId(assignedByUserId);
        projectAccess.setAccessAssignedAt(now);
        projectAccess.setRemovedByUserId(null);
        projectAccess.setRevokedAccessAt(null);

        return projectAccessRepository.save(projectAccess);
    }

    //! READ
    public List<ProjectAccess> findActiveAccessByProjectId(UUID projectId) {
        return projectAccessRepository.findByProjectIdAndRevokedAccessAtIsNull(projectId);
    }

    public Optional<ProjectAccess> findActiveAccessByProjectIdAndUserId(UUID projectId, UUID userId) {
        return projectAccessRepository.findByProjectIdAndUserIdAndRevokedAccessAtIsNull(projectId, userId);
    }

    public List<ProjectAccess> listActiveAccessByProjectIdAndRole(UUID projectId, ProjectRole projectRole) {
        return projectAccessRepository.findByProjectIdAndProjectRoleAndRevokedAccessAtIsNull(projectId, projectRole);
    }

    //! UPDATE
    public ProjectAccess revokeAccess(UUID projectId, UUID userId, UUID removedByUserId) {

        if (projectId == null || userId == null || removedByUserId == null) {
            throw new IllegalArgumentException("Missing required fields!");
        }

        // * Admin-only
        authzService.mustBeAdmin(userId);

        ProjectAccess projectAccess = this.findActiveAccessByProjectIdAndUserId(projectId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Active access was not found!"));

        projectAccess.setRemovedByUserId(removedByUserId);
        projectAccess.setRevokedAccessAt(Instant.now());

        return projectAccessRepository.save(projectAccess);
    }

    //! "DELETE"
}
