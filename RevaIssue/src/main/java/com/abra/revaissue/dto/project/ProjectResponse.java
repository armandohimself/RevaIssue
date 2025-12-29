package com.abra.revaissue.dto.project;

import java.time.Instant;
import java.util.UUID;

import com.abra.revaissue.enums.ProjectStatus;

public record ProjectResponse(
    UUID projectId, 
    String projectName, 
    String projectDescription, 
    ProjectStatus projectStatus, 
    UUID createdByUserId, 
    Instant createdAt, 
    Instant updatedAt
) {}