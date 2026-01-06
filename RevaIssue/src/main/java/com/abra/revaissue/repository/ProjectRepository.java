package com.abra.revaissue.repository;

import java.util.List;
import java.util.UUID;

import com.abra.revaissue.entity.Project;
import com.abra.revaissue.enums.ProjectStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByProjectIdIn(List<UUID> projectIds);

    List<Project> findByCreatedByUserId(UUID userId);

    List<Project> findByProjectStatus(ProjectStatus projectStatus);

    Project findByProjectId(UUID projectId);

    boolean existsByProjectName(String projectName);
}
