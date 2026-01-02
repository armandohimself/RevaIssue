package com.abra.revaissue.dto.project;

import java.util.UUID;
import com.abra.revaissue.enums.ProjectRole;

public record GrantProjectAccessRequest (
    UUID projectId, 
    UUID userId,
    ProjectRole projectRole, 
    UUID assignedByUserId
) {}