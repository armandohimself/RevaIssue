package com.abra.revaissue.dto.project;

import com.abra.revaissue.entity.Project;

public final class ProjectMapper {
    private ProjectMapper() {}

    public static ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
            project.getProjectId(), 
            project.getProjectName(), 
            project.getProjectDescription(),
            project.getProjectStatus(), 
            project.getCreatedByUserId(), 
            project.getCreatedAt(), 
            project.getUpdatedAt());
    }
}