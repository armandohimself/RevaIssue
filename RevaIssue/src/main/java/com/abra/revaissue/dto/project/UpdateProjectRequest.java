package com.abra.revaissue.dto.project;

import com.abra.revaissue.enums.ProjectStatus;

public record UpdateProjectRequest(
    String projectName, 
    String projectDescription, 
    ProjectStatus projectStatus
) {}