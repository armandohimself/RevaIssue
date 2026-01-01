package com.abra.revaissue.dto.project;

import java.util.UUID;

import com.abra.revaissue.enums.ProjectRole;

public record ProjectAccessResponse(
    UUID projectAccessId, 
    UUID projectId, 
    ProjectRole projectRole, 
    UUID userId
) {}