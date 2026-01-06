package com.abra.revaissue.controller;

import java.util.List;
import java.util.UUID;

import com.abra.revaissue.dto.project.AdminProjectResponse;
import com.abra.revaissue.dto.project.CreateProjectRequest;
import com.abra.revaissue.dto.project.ProjectMapper;
import com.abra.revaissue.dto.project.ProjectResponse;
import com.abra.revaissue.dto.project.UpdateProjectRequest;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.service.AuthzService;
import com.abra.revaissue.service.ProjectService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final AuthzService authzService;
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService, AuthzService authzService) {
        this.projectService = projectService;
        this.authzService = authzService;
    }

    //! POST
    @PostMapping
    public ResponseEntity<ProjectResponse> create(
        @RequestBody CreateProjectRequest request,
        @RequestHeader(name="Authorization") String token
    ) {

        UUID actingUserId = authzService.actingUserId(token);

        Project newProject = new Project();
        newProject.setProjectName(request.projectName());
        newProject.setProjectDescription(request.projectDescription());

        Project createdProject = projectService.create(newProject, actingUserId);

        return ResponseEntity.ok(ProjectMapper.toResponse(createdProject));
    }

    //! GET
    @GetMapping()
    public ResponseEntity<List<ProjectResponse>> listAll(
        @RequestHeader(name="Authorization") String token
    ) {

        // UUID actingUserId = authzService.actingUserId(token);

        List<ProjectResponse> projectList = projectService.getAll()
                .stream()
                .map(ProjectMapper::toResponse)
                .toList();

        return ResponseEntity.ok(projectList);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getById(
        @PathVariable UUID projectId,
        @RequestHeader(name="Authorization") String token
    ) {

        // UUID actingUserId = authzService.actingUserId(token);

        Project getProject = projectService.getById(projectId);
        return ResponseEntity.ok(ProjectMapper.toResponse(getProject));
    }

    @GetMapping("/{projectId}/admin")
    public ResponseEntity<AdminProjectResponse> getAdminProject(
        @PathVariable UUID projectId,
        @RequestHeader(name="Authorization") String token
    ) {

        UUID actingUserId = authzService.actingUserId(token);
        authzService.mustBeAdmin(actingUserId);

        Project getProject = projectService.getById(projectId);
        return ResponseEntity.ok(ProjectMapper.toAdminResponse(getProject));
    }

    // //! PATCH
    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> update(
        @PathVariable UUID projectId,
        @RequestBody UpdateProjectRequest updateProjectRequest,
        @RequestHeader(name="Authorization") String token
    ) {

        UUID actingUserId = authzService.actingUserId(token);

        Project updatedProject = projectService.update(projectId, updateProjectRequest, actingUserId);

        return ResponseEntity.ok(ProjectMapper.toResponse(updatedProject));
    }

    //! "DELETE"
    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
        @PathVariable UUID projectId,
        @RequestHeader(name="Authorization") String token
    ) {
        UUID actingUserId = authzService.actingUserId(token);

        projectService.archive(projectId, actingUserId);

        return ResponseEntity.noContent().build();
    }
}
