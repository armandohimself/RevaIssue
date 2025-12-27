package com.abra.revaissue.repository;

import java.util.List;
import java.util.UUID;

import com.abra.revaissue.entity.Project;
import com.abra.revaissue.enums.ProjectStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByCreatedByUserId(UUID userId);

    List<Project> findByProjectStatus(ProjectStatus projectStatus);

    boolean existsByProjectName(String projectName);
}