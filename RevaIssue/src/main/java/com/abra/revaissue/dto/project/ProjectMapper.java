package com.abra.revaissue.dto.project;

import com.abra.revaissue.entity.Project;

public final class ProjectMapper {
    private ProjectMapper() {}

    public static ProjectResponse toResponse(Project p) {
        return new ProjectResponse(
                p.getProjectId(),
                p.getProjectName(),
                p.getProjectDescription(),
                p.getProjectStatus(),
                p.getCreatedByUserId(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
