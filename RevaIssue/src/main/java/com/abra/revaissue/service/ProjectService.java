package com.abra.revaissue.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.abra.revaissue.dto.project.UpdateProjectRequest;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.ProjectStatus;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.ProjectRepository;
import com.abra.revaissue.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    //! CREATE

    // * Create project (Admin-only)
    public Project create(Project project, UUID userId) {
        // Guard rails
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null!");
        }

        if (project.getProjectName() == null || project.getProjectName().isBlank()) {
            throw new IllegalArgumentException("Project name cannot be null or blank!");
        }

        if (userId == null) {
            throw new IllegalArgumentException("No userId in the header!");
        }

        // * Admin-only
        checkIfRoleAdmin(userId);
        
        Instant now = Instant.now();
        
        // Set defaults
        project.setCreatedByUserId(userId);

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

    //! READ

    // * Get all projects as list
    public List<Project> getAll() {
        // TODO: Update to grab views based on roles
        return projectRepository.findAll();
    }

    // * Get specific project by it's Id.
    public Project getById(UUID projectId) {
        // TODO: Update to include userId
        return checkIfProjectExists(projectId);
    }

    // * Get list of projects by status
    public List<Project> getByStatus(ProjectStatus status) {
        return projectRepository.findByProjectStatus(status);
    }

    //! UPDATE

    // * Update project details
    public Project update(UUID projectId, UpdateProjectRequest updateProjectRequest, UUID userId) {
        // Guard rails
        if (projectId == null) throw new IllegalArgumentException("Project Id is required to update a project!");

        if (updateProjectRequest == null) throw new IllegalArgumentException("updateProjectRequest is required to update a project!");

        if (userId == null) throw new IllegalArgumentException("User Id is required to update a project!");

        // * Admin-only
        checkIfRoleAdmin(userId);
        
        Project project = checkIfProjectExists(projectId);
        
        Instant now = Instant.now();

        // Patch Updates
        applyPatchFields(project, updateProjectRequest);
        applyStatusTransition(project, updateProjectRequest.projectStatus(), userId, now);

        // Audit Timestamp
        project.setUpdatedAt(now);

        return projectRepository.save(project);
    }

    //! DELETE

    

    //! Helper Functions
    public void checkIfRoleAdmin(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        if(user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Admin privilege required to perform action!");
        }
    }

    private Project checkIfProjectExists(UUID projectId) {
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
            // TODO: Create a record
            project.setProjectName(updateProjectRequest.projectName());
        }

        // * Project Description
        if (updateProjectRequest.projectDescription() != null) {
            // TODO: Create a record
            project.setProjectDescription(updateProjectRequest.projectDescription());
        }
    }

    private void applyStatusTransition(Project project, ProjectStatus newProjectStatus, UUID userId, Instant now) {
        // Guard rail
        if (newProjectStatus == null) return;
        // no change, nothing to audit
        if (newProjectStatus == project.getProjectStatus()) return; 

        // * Project Status
        // TODO: Create record of who made the change + update status change
        project.setProjectStatus(newProjectStatus);
        project.setStatusUpdatedByUserId(userId);

        // * ARCHIVED & RE-ACTIVE Status
        if (newProjectStatus == ProjectStatus.ARCHIVED) {
            // TODO: Create a record
            project.setArchivedByUserId(userId);
            project.setArchivedAt(now);
        } else if (newProjectStatus == ProjectStatus.ACTIVE) {
            // TODO: Create a record
            project.setArchivedByUserId(null);
            project.setArchivedAt(null);
        }
    }
}