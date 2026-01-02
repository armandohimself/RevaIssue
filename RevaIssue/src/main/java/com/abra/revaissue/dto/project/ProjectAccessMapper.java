package com.abra.revaissue.dto.project;

import com.abra.revaissue.entity.ProjectAccess;

public final class ProjectAccessMapper {
    private ProjectAccessMapper() {}

    public static ProjectAccessResponse toResponse(ProjectAccess projectAccess) {
        return new ProjectAccessResponse(
            projectAccess.getProjectAccessId(), 
            projectAccess.getProjectId(), 
            projectAccess.getProjectRole(), 
            projectAccess.getUserId()
        );
    }
}