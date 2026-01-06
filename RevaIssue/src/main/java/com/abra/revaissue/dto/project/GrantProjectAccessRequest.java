package com.abra.revaissue.dto.project;

import java.util.UUID;
import com.abra.revaissue.enums.ProjectRole;

/**
 * Removed projectId to avoid calling it in the body and URL
 * Removed assignedByUserId because we have trust issues with people who lie.
 */
public record GrantProjectAccessRequest (
    UUID userId,
    ProjectRole projectRole
) {}
