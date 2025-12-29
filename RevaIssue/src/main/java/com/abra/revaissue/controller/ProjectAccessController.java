package com.abra.revaissue.controller;

import com.abra.revaissue.service.ProjectAccessService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/project-access")
public class ProjectAccessController {
    
    private final ProjectAccessService projectAccessService;

    public ProjectAccessController(ProjectAccessService projectAccessService) {
        this.projectAccessService = projectAccessService;
    }

    // Admin grants access on project
    // list all members on project
    // admin revokes access on project




}
