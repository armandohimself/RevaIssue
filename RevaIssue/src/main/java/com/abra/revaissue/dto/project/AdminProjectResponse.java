package com.abra.revaissue.dto.project;

import java.time.Instant;
import java.util.UUID;

import com.abra.revaissue.enums.ProjectStatus;

public record AdminProjectResponse(
    UUID projectId,
    String projectName,
    String projectDescription,
    ProjectStatus projectStatus,
    UUID createdByUserId,
    UUID statusUpdatedByUserId,
    UUID archivedByUserId,
    Instant createdAt,
    Instant updatedAt,
    Instant archivedAt
) {}
