package com.abra.revaissue.controller;

import java.util.List;
import java.util.UUID;

import com.abra.revaissue.dto.project.GrantProjectAccessRequest;
import com.abra.revaissue.dto.project.ProjectAccessMapper;
import com.abra.revaissue.dto.project.ProjectAccessResponse;
import com.abra.revaissue.entity.ProjectAccess;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.service.AuthzService;
import com.abra.revaissue.service.ProjectAccessService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Because access cannot be granted to projects that were not created, you can assume always a projectId must exist.
 * Our ProjectAccessResponse will have a shape of: UUID projectAccessId, UUID projectId, ProjectRole projectRole, UUID userId
 */
@RestController
@RequestMapping("/api/projects/{projectId}/access")
public class ProjectAccessController {

    private final AuthzService authzService;
    private final ProjectAccessService projectAccessService;

    public ProjectAccessController(ProjectAccessService projectAccessService, AuthzService authzService) {
        this.projectAccessService = projectAccessService;
        this.authzService = authzService;
    }

    //! GET
    @GetMapping()
    public ResponseEntity<List<ProjectAccessResponse>> listProjectMembers(
        @PathVariable UUID projectId,
        @RequestHeader(name="Authorization") String token
    ) {

        UUID actingUserId = authzService.actingUserId(token);

        List<ProjectAccessResponse> projectAccessList = projectAccessService.findActiveAccessByProjectId(projectId, actingUserId)
            .stream()
            .map(ProjectAccessMapper::toResponse)
            .toList();

        return ResponseEntity.ok(projectAccessList);
    }

    //! GET
    @GetMapping("/all")
    public ResponseEntity<List<User>> getProjectMembers(
            @PathVariable UUID projectId,
            @RequestHeader(name="Authorization") String token
    ) {

        UUID actingUserId = authzService.actingUserId(token);

        return ResponseEntity.ok(projectAccessService.findMembersByProjectId(projectId, actingUserId));
    }

    /**
     * GrantProjectAccessRequest will have the shape of: UUID userId, ProjectRole projectRole, UUID assignedByUserId
     */
    //! POST
    @PostMapping()
    public ResponseEntity<ProjectAccessResponse> grant(
            @PathVariable UUID projectId,
            @RequestBody GrantProjectAccessRequest request,
            @RequestHeader(name="Authorization") String token
        ) {

        UUID actingUserId = authzService.actingUserId(token);

        ProjectAccess incoming = new ProjectAccess();
        incoming.setProjectId(projectId);
        incoming.setUserId(request.userId());
        incoming.setProjectRole(request.projectRole());

        ProjectAccess createdProjectAccess = projectAccessService.assignAccess(incoming, actingUserId);

        return ResponseEntity.ok(ProjectAccessMapper.toResponse(createdProjectAccess));
    }

    //! DELETE
    /**
     * What ResponseEntity.noContent().build() means:
     * Request succeeded, but I'm not sending any response body back.
     * It's gone, it's revoked say no more.
     *      ResponseEntity = Spring’s “full HTTP response builder” (status + headers + body)
     *      .noContent() = sets status code to 204
     *      .build() = “finalize it and return it”
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> revoke(
        @PathVariable UUID projectId,
        @PathVariable UUID userId,
        @RequestHeader(name="Authorization") String token
    ) {
        UUID actingUserId = authzService.actingUserId(token);

        projectAccessService.revokeAccess(projectId, userId, actingUserId);
        return ResponseEntity.noContent().build();
    }
}
