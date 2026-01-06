package com.abra.revaissue.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.abra.revaissue.dto.project.UpdateProjectRequest;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.enums.ProjectStatus;
import com.abra.revaissue.repository.ProjectRepository;

// import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AuthzService authzService;

    public ProjectService(ProjectRepository projectRepository, AuthzService authzService) {
        this.projectRepository = projectRepository;
        this.authzService = authzService;
    }

    // ! CREATE

    // * Create project (Admin-only)
    public Project create(Project project, UUID actingUserId) {
        // Guard rails
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null!");
        }

        if (project.getProjectName() == null || project.getProjectName().isBlank()) {
            throw new IllegalArgumentException("Project name cannot be null or blank!");
        }

        if (actingUserId == null) {
            throw new IllegalArgumentException("No actingUserId in the header!");
        }

        // * Admin-only
        authzService.mustBeAdmin(actingUserId);

        Instant now = Instant.now();

        // Set defaults
        project.setCreatedByUserId(actingUserId);

        if (project.getProjectStatus() == null) {
            project.setProjectStatus(ProjectStatus.ACTIVE);
        }

        // Audit Timestamp
        if (project.getCreatedAt() == null) {
            project.setCreatedAt(now);
        }

        project.setUpdatedAt(now);

        return projectRepository.save(project);
    }

    // ! READ

    // * Get all projects as list
    public List<Project> getAll() {
        // TODO: Update to grab views based on roles
        return projectRepository.findAll();
    }

    // * Get specific project by it's Id.
    public Project getById(UUID projectId) {
        // TODO: Update to include actingUserId
        return projectMustExist(projectId);
    }

    // * Get list of projects by status
    public List<Project> getByStatus(ProjectStatus status) {
        return projectRepository.findByProjectStatus(status);
    }

    //! UPDATE
    // * Update project details
    public Project update(UUID projectId, UpdateProjectRequest updateProjectRequest, UUID actingUserId) {
        // Guard rails
        if (projectId == null)
            throw new IllegalArgumentException("Project Id is required to update a project!");

        if (updateProjectRequest == null)
            throw new IllegalArgumentException("updateProjectRequest is required to update a project!");

        if (actingUserId == null)
            throw new IllegalArgumentException("User Id is required to update a project!");

        // * Admin-only
        authzService.mustBeAdmin(actingUserId);

        Project project = projectMustExist(projectId);

        Instant now = Instant.now();

        // Patch Updates
        applyPatchFields(project, updateProjectRequest);
        applyStatusTransition(project, updateProjectRequest.projectStatus(), actingUserId, now);

        // Audit Timestamp
        project.setUpdatedAt(now);

        return projectRepository.save(project);
    }

    //! DELETE
    public void archive(UUID projectId, UUID actingUserId) {
        authzService.mustBeAdmin(actingUserId);
        Project project = projectMustExist(projectId);

        Instant now = Instant.now();

        project.setProjectStatus(ProjectStatus.ARCHIVED);
        project.setArchivedByUserId(actingUserId);
        project.setArchivedAt(now);
        project.setStatusUpdatedByUserId(actingUserId);
        project.setUpdatedAt(now);

        projectRepository.save(project);
    }

    // ! Helper Functions
    private Project projectMustExist(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found!"));

        return project;
    }

    private void applyPatchFields(Project project, UpdateProjectRequest updateProjectRequest) {
        // Guard rail

        // * Project Name
        if (updateProjectRequest.projectName() != null) {
            if (updateProjectRequest.projectName().isBlank()) {
                throw new IllegalArgumentException("projectName cannot be blank!");
            }

            project.setProjectName(updateProjectRequest.projectName());
        }

        // * Project Description
        if (updateProjectRequest.projectDescription() != null) {
            project.setProjectDescription(updateProjectRequest.projectDescription());
        }
    }

    private void applyStatusTransition(Project project, ProjectStatus newProjectStatus, UUID actingUserId, Instant now) {
        // Guard rail
        if (newProjectStatus == null)
            return;
        // no change, nothing to audit
        if (newProjectStatus == project.getProjectStatus())
            return;

        // ARCHIVED PROJECT
        if (newProjectStatus == ProjectStatus.ARCHIVED) {


            /**
             * TODO: Call ProjectAccessService or ProjectAccessRepo?
             * TODO: "remove" members from project aka revokedAccessAt to Instant.now()
             *
             * TODO: Separately, Log Transaction needs to added for the status change
             * TODO: && removing everyone from this project as a result of the archive
             *
             * Comeback and continue as normal setting archived id and archived at to now
             */

            project.setArchivedByUserId(actingUserId);
            project.setArchivedAt(now);

            // RE-ACTIVE PROJECT
        } else if (newProjectStatus == ProjectStatus.ACTIVE) {

            /**
             * TODO: Call Log Transaction to record that admin just revived a project
             * * No one should be added so far to this project thus no need to make revoked at null unless you want to add everyone who was on this project back on accidentally
             * Comeback and continue as normal.
             */

            project.setArchivedByUserId(null);
            project.setArchivedAt(null);
        }

        /**
         * Should additions to ProjectStatus come up, we can update normally without issue.
         * Regardless, a project status will be updated and the user who updated the status gets updated
         */
        project.setProjectStatus(newProjectStatus);
        project.setStatusUpdatedByUserId(actingUserId);


    }
}
