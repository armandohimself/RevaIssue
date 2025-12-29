package com.abra.revaissue.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.abra.revaissue.dto.project.CreateProjectRequest;
import com.abra.revaissue.dto.project.ProjectMapper;
import com.abra.revaissue.dto.project.ProjectResponse;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.service.ProjectService;

@RestController
// means: this class handles web requests AND the return values are written as
// JSON by default.
@RequestMapping("/api/projects")
// sets the base path for all methods.

public class ProjectControllerWithDTO {

    private final ProjectService projectService;

    public ProjectControllerWithDTO(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping // matches POST requests at that path.
    public ResponseEntity<ProjectResponse> create(@RequestBody CreateProjectRequest req) {
        // @RequestBody says: “take the incoming JSON body and map it to this parameter
        // type.”
        // req is JSON coming in => @RequestBody + Jackson deserialization => Shapes it
        // into CreateProjectRequest

        Project p = new Project();
        // Create an instance of Project object
        p.setProjectName(req.projectName());
        p.setProjectDescription(req.projectDescription());
        p.setCreatedByUserId(req.createdByUserId());
        // Map DTO (which was req and before was JSON) to Entity

        Project created = projectService.create(p);
        // Entity is passed, service (biz logic validates, saves, etc).
        return ResponseEntity.ok(ProjectMapper.toResponse(created));
        // We then map the Entity to a DTO with ProjectMapper => Then it becomes JSON
        // with ResponseEntity by using serialization on the DTO

        /**
         * ResponseEntity.ok(someObject): means send back HTTP response of 200 OK with a
         * serialized someObject from Entity to JSON in the response
         * ProjectMapper.toResponse(created): means take Project Entity we just saved,
         * and turn it into the
         * 
         */
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
