package com.abra.revaissue.controller;

import java.util.List;
import java.util.UUID;

import com.abra.revaissue.dto.project.CreateProjectRequest;
import com.abra.revaissue.dto.project.ProjectMapper;
import com.abra.revaissue.dto.project.ProjectResponse;
import com.abra.revaissue.dto.project.UpdateProjectRequest;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.service.ProjectService;

import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    //! POST
    @PostMapping
    public ResponseEntity<ProjectResponse> create(@RequestBody CreateProjectRequest request, @RequestHeader("X-User-Id") UUID userId) {

        Project newProject = new Project();
        newProject.setProjectName(request.projectName());
        newProject.setProjectDescription(request.projectDescription());

        Project createdProject = projectService.create(newProject, userId);

        return ResponseEntity.ok(ProjectMapper.toResponse(createdProject));
    }

    //! GET
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listAll() {

        List<ProjectResponse> projectList = projectService.getAll()
                .stream()
                .map(ProjectMapper::toResponse)
                .toList();

        return ResponseEntity.ok(projectList);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable UUID projectId) {

        Project getProject = projectService.getById(projectId);
        return ResponseEntity.ok(ProjectMapper.toResponse(getProject));
    }

    //! PUT
    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> update(@PathVariable UUID projectId,
            @RequestBody UpdateProjectRequest updateProjectRequest, @RequestHeader("X-User-Id") UUID userId) {
        Project updatedProject = projectService.update(projectId, updateProjectRequest, userId);

        return ResponseEntity.ok(ProjectMapper.toResponse(updatedProject));
    }

    //! "DELETE"
    // @DeleteMapping("/{projectId}")
}
