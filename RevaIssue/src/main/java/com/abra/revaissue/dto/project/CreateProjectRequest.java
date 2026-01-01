package com.abra.revaissue.dto.project;

public record CreateProjectRequest(
    String projectName, 
    String projectDescription
) {}