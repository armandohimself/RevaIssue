package com.abra.revaissue.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.abra.revaissue.entity.Project;
import com.abra.revaissue.service.ProjectService;

/**
 * ? @RestController -> Combines @Controller + @ResponseBody
 * Handles HTTP requests and returns JSON
 * 
 * ? @RequestMapping("api/projects")
 * All routes in here start with this base path
 * 
 * ? @PostMapping, @GetMapping, @PatchMapping, @DeleteMapping
 * This method runs when that HTTP verb + path matches.
 * 
 * ? @PathVariable
 * Grab {projectId} from the URL
 * 
 * ? @RequestBody
 * Parse incoming JSON into a Java Object
 * 
 * ? @RequestParam
 * Read ?status=ACTIVE from the URL
 * 
 * ? @ResponseEntity
 * Lets you control status codes + headers + body
 */

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // ! POST /api/projects
    @PostMapping
    public ResponseEntity<Project> create(@RequestBody Project project) {

        // ! PRINT
        System.out.println("Creating project: " + project);
        
        Project created = projectService.create(project);
        return ResponseEntity.ok(created);
    }

    // ! GET /api/projects/{projectId}
    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getById(@PathVariable UUID projectId) {
        Project p = projectService.getById(projectId);
        return ResponseEntity.ok(p);
    }

    // ! GET /api/projects
    @GetMapping
    public ResponseEntity<List<Project>> listAll() {
        return ResponseEntity.ok(projectService.getAll());
    }
}
/**
 * ✅ This works fast.
 * ⚠️ But it lets callers send fields you might not want them to set (timestamps/status/etc).
 */



/**
 * ! Controller DTO Version
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.abra.revaissue.dto.project.CreateProjectRequest;
import com.abra.revaissue.dto.project.ProjectMapper;
import com.abra.revaissue.dto.project.ProjectResponse;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.service.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@RequestBody CreateProjectRequest req) {

        Project p = new Project();
        p.setProjectName(req.projectName());
        p.setProjectDescription(req.projectDescription());
        p.setCreatedByUserId(req.createdByUserId());

        Project created = projectService.create(p);
        return ResponseEntity.ok(ProjectMapper.toResponse(created));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable UUID projectId) {
        Project p = projectService.getById(projectId);
        return ResponseEntity.ok(ProjectMapper.toResponse(p));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listAll() {
        List<ProjectResponse> out = projectService.getAll()
                .stream()
                .map(ProjectMapper::toResponse)
                .toList();

        return ResponseEntity.ok(out);
    }
}
 */