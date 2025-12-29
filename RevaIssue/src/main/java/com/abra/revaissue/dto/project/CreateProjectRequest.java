package com.abra.revaissue.dto.project;

import java.util.UUID;

public record CreateProjectRequest(
    String projectName, 
    String projectDescription, 
    UUID createdByUserId
) {}
