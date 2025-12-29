package com.abra.revaissue.controller;

import java.util.List;
import java.util.UUID;

import com.abra.revaissue.dto.project.CreateProjectRequest;
import com.abra.revaissue.dto.project.ProjectMapper;
import com.abra.revaissue.dto.project.ProjectResponse;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.service.ProjectService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/projects")
public class ProjectController {
    private final ProjectService projectService;

    private ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // ! POST
    @PostMapping
    public ResponseEntity<ProjectResponse> create(@RequestBody CreateProjectRequest request) {

        Project newProject = new Project();
        newProject.setProjectName(request.projectName());
        newProject.setProjectDescription(request.projectDescription());
        newProject.setCreatedByUserId(request.createdByUserId());

        Project createdProject = projectService.create(newProject);

        return ResponseEntity.ok(ProjectMapper.toResponse(createdProject));
    }

    // ! GET
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable UUID projectId) {

        Project getProject = projectService.getById(projectId);
        return ResponseEntity.ok(ProjectMapper.toResponse(getProject));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listAll() {

        List<ProjectResponse> projectList = projectService.getAll()
            .stream()
            .map(ProjectMapper::toResponse)
            .toList();

        return ResponseEntity.ok(projectList);
    }

    // ! PUT

    // ! "DELETE"

}
