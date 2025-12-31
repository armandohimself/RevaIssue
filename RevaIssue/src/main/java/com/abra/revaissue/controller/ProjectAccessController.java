package com.abra.revaissue.controller;

import java.util.List;
import java.util.UUID;

import com.abra.revaissue.dto.project.GrantProjectAccessRequest;
import com.abra.revaissue.dto.project.ProjectAccessMapper;
import com.abra.revaissue.dto.project.ProjectAccessResponse;
import com.abra.revaissue.entity.ProjectAccess;
import com.abra.revaissue.service.ProjectAccessService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project-access")
public class ProjectAccessController {
    
    private final ProjectAccessService projectAccessService;

    public ProjectAccessController(ProjectAccessService projectAccessService) {
        this.projectAccessService = projectAccessService;
    }

    //! CREATE
    @PostMapping
    public ResponseEntity<ProjectAccessResponse> grantProjectAccess(
            @RequestBody GrantProjectAccessRequest request, 
            @RequestHeader("X-User-Id") UUID adminUserId
        ) {
        ProjectAccess incoming = new ProjectAccess();

        incoming.setProjectId(request.projectId());
        incoming.setUserId(request.userId());
        incoming.setProjectRole(request.projectRole());
        incoming.setAssignedByUserId(adminUserId);

        ProjectAccess createdProjectAccess = projectAccessService.assignAccess(incoming, adminUserId);

        return ResponseEntity.ok(ProjectAccessMapper.toResponse(createdProjectAccess));
    }

    //! READ
    // list all members on project
    @GetMapping("/{projectId}")
    public ResponseEntity<List<ProjectAccessResponse>> getAllGrantedAccessByProjectId(
        @PathVariable UUID projectId, 
        @RequestHeader("X-User-Id") UUID adminUserId
    ) {
        List<ProjectAccessResponse> projectAccessList = projectAccessService.findActiveAccessByProjectId(projectId, adminUserId)
            .stream()
            .map(ProjectAccessMapper::toResponse)
            .toList();

        return ResponseEntity.ok(projectAccessList);
    }

    // A member may want to see all the project they are on
    // Validate member who may not be an admin
    // @GetMapping("/{userId}")


    //! UPDATE
    // Admin revokes access on project
    @PatchMapping("/revoke/{projectId}/{targetUserId}")
    public ResponseEntity<ProjectAccessResponse> revokeProjectAccess(
        @PathVariable UUID projectId, 
        @PathVariable UUID targetUserId, 
        @RequestHeader("X-User-Id") UUID adminUserId
    ) {

        ProjectAccess updatedProjectAccess = projectAccessService.revokeAccess(projectId, targetUserId, adminUserId);
        return ResponseEntity.ok(ProjectAccessMapper.toResponse(updatedProjectAccess));
    }
    


    //! DELETE




}
