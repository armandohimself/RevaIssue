package com.abra.revaissue.service;

import java.time.Instant;
import java.util.List;
// import java.util.Optional;
import java.util.UUID;

import com.abra.revaissue.entity.Project;
import com.abra.revaissue.enums.ProjectStatus;
import com.abra.revaissue.repository.ProjectRepository;

import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // CREATE
    public Project create(Project project) {
        // Guard rails
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null!");
        }

        if (project.getProjectName() == null || project.getProjectName().isBlank()) {
            throw new IllegalArgumentException("Project name cannot be null or blank!");
        }

        if (project.getCreatedByUserId() == null) {
            throw new IllegalArgumentException("User's ID who created project cannot be null!");
        }

        // Check User "Credentials"/Role? || is that done in the API level?

        Instant now = Instant.now();

        // Set defaults
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

    // READ
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    public Project getById(UUID projectId) {
        // Could swap for Supplier function later
        var opt = projectRepository.findById(projectId);

        if (opt.isPresent()) {
            return opt.get();
        }

        throw new IllegalArgumentException("Project not found!");
    }

    public List<Project> getByStatus(ProjectStatus status) {
        return projectRepository.findByProjectStatus(status);
    }

    // UPDATE
    // public Project update(UUID projectId, UpdateProjectRequestDTO dto) {

    //     TODO: Need to create DTO

    //     Project project = getById(projectId);

    //     Update project

    //     return project;
    // }

    public Project updateByStatus(UUID projectId, ProjectStatus newStatus, UUID statusUpdatedByUserId) {
        Project project = getById(projectId);

        Instant now = Instant.now();

        project.setProjectStatus(newStatus);
        project.setStatusUpdatedByUserId(statusUpdatedByUserId);
        project.setUpdatedAt(now);

        // "DELETE"/ARCHIVE
        if (newStatus == ProjectStatus.ARCHIVED) {
            project.setArchivedAt(now);
            project.setArchivedByUserId(statusUpdatedByUserId);
        }

        return projectRepository.save(project);
    }

}