package com.abra.revaissue.service;

/**
 * ? What Is A Service?
 * It’s just a class that receives the Repository we created, uses the repo methods, and optionally sets rules.
 * JARGON: @Service layer: validations, transactions, orchestration.
 * ENGLISH: Services are your “business rules layer.”
 */

import com.abra.revaissue.repository.ProjectRepository;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.enums.ProjectStatus;

import org.springframework.stereotype.Service;
/**
 * ? Stereotypes?
 * * A “stereotype” is a label Spring uses to recognize “this class has a special job
 */

import java.time.Instant;
import java.util.List;
import java.util.UUID;
// import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    /**
     * ? final means:
     * * This reference can’t be reassigned.
     * Once we set projectRepo in the constructor, we will never point it to a
     * different repo.
     * This is called encapsulation: When you don’t want other classes reaching in
     * and messing with your internal fields.
     */

    // ! Constructor
    public ProjectService(ProjectRepository projectRepository) {
        /**
         * Constructor injection:
         * * When Spring creates ProjectService, it will also provide the
         * ProjectRepository automatically.
         * Sees that ProjectRepository is also a bean (from Spring Data creating it)
         * ! sees your constructor needs one.
         * ! It injects it (constructor injection).
         */
        this.projectRepository = projectRepository;
        /**
         * * Why?
         * Because your parameter name (projectRepository) matches your field name
         * (this.projectRepository).
         */
    }

    public Project create(Project project) {
        // Guard clauses = fail fast
        if (project == null)
            throw new IllegalArgumentException("Project is required");
        if (project.getProjectName() == null || project.getProjectName().isBlank())
            throw new IllegalArgumentException("projectName is required");
        if (project.getCreatedByUserId() == null)
            throw new IllegalArgumentException("createdByUserId is required");

        Instant now = Instant.now();

        // Set defaults
        if (project.getProjectStatus() == null) {
            project.setProjectStatus(ProjectStatus.ACTIVE);
        }

        // Audit timestamps
        if (project.getCreatedAt() == null)
            project.setCreatedAt(now);
        project.setUpdatedAt(now);

        // Persist -> save() is “free” from JpaRepository
        return projectRepository.save(project);
    }

    public Project getById(UUID projectId) {
        // Optional -> either contains Project or empty
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    public List<Project> getByStatus(ProjectStatus status) {
        return projectRepository.findByProjectStatus(status);
    }

    public Project updateStatus(UUID projectId, ProjectStatus newStatus, UUID statusUpdatedByUserId) {
        Project p = getById(projectId);

        p.setProjectStatus(newStatus);
        p.setStatusUpdatedByUserId(statusUpdatedByUserId);
        p.setUpdatedAt(Instant.now());

        if (newStatus == ProjectStatus.ARCHIVED) {
            p.setArchivedAt(Instant.now());
            p.setArchivedByUserId(statusUpdatedByUserId);
        }

        return projectRepository.save(p);
    }

}