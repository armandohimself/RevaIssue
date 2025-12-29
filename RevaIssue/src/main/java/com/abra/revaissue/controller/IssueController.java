package com.abra.revaissue.controller;

import com.abra.revaissue.dto.IssueCreateDTO;
import com.abra.revaissue.dto.IssueResponseDTO;
import com.abra.revaissue.dto.IssueUpdateDTO;
import com.abra.revaissue.entity.Issue;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import com.abra.revaissue.enums.IssueStatus;
import com.abra.revaissue.service.IssueService;
import com.abra.revaissue.service.ProjectService;
import com.abra.revaissue.service.UserService;
import com.abra.revaissue.util.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class IssueController {
    private final IssueService issueService;
    private final UserService userService;
    private final ProjectService projectService;
    private final JwtUtility jwtUtility;
    @Autowired
    public IssueController(IssueService issueService, UserService userService, ProjectService projectService, JwtUtility jwtUtility){
        this.issueService = issueService;
        this.userService = userService;
        this.projectService = projectService;
        this.jwtUtility = jwtUtility;
    }

    @PostMapping("/projects/{projectId}/issues")
    public ResponseEntity<IssueResponseDTO> createIssue(@PathVariable UUID projectId, @RequestBody IssueCreateDTO dto, @RequestHeader(name = "Authorization") String token){
        User actingUser = getActingUser(token);
        Project project = projectService.getById(projectId);
        IssueResponseDTO created = issueService.createIssue(dto, project, actingUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @GetMapping("/projects/{projectId}/issues")
    public ResponseEntity<List<IssueResponseDTO>> getIssuesForProject(@PathVariable UUID projectId,
                                           @RequestParam(required = false)IssueStatus status,
                                           @RequestParam(required = false) IssueSeverity severity,
                                           @RequestParam(required = false)IssuePriority priority){
        Project project = projectService.getById(projectId);
        List<IssueResponseDTO> resultList;
        if(status != null){
            resultList = issueService.getIssuesByProjectAndStatus(project, status);
        }
        else if(severity != null){
            resultList = issueService.getIssuesByProjectAndSeverity(project, severity);
        }
        else if(priority != null){
            resultList = issueService.getIssuesByProjectAndPriority(project, priority);
        }
        else{
            resultList = issueService.getIssuesByProject(project);
        }
        return ResponseEntity.ok(resultList);
    }
    @GetMapping("/issues/{issueId}")
    public ResponseEntity<IssueResponseDTO> getIssueById(@PathVariable UUID issueId){
        return ResponseEntity.ok(issueService.getIssueById(issueId));
    }
    @GetMapping("/users/{userId}/assigned-issues")
    public ResponseEntity<List<IssueResponseDTO>> getIssuesAssignedToUser(@PathVariable UUID userId){
        User user = userService.getUserByUUID(userId);
        return ResponseEntity.ok(issueService.getIssuesAssignedToUser(user));
    }
    @GetMapping("/users/{userId}/created-issues")
    public ResponseEntity<List<IssueResponseDTO>> getIssuesCreatedByUser(@PathVariable UUID userId){
        User user = userService.getUserByUUID(userId);
        return ResponseEntity.ok(issueService.getIssuesCreatedByUser(user));
    }

    @PutMapping("/issues/{issueId}")
    public ResponseEntity<IssueResponseDTO> updateIssue(@PathVariable UUID issueId, @RequestBody IssueUpdateDTO dto, @RequestHeader(name = "Authorization") String token){
        User actingUser = getActingUser(token);
        IssueResponseDTO updated = issueService.updateIssue(issueId, dto, actingUser);
        return ResponseEntity.ok(updated);
    }
    @PutMapping("/issues/{issueId}/assign/{userId}")
    public ResponseEntity<IssueResponseDTO> assignDeveloper(@PathVariable UUID issueId, @PathVariable UUID userId, @RequestHeader(name = "Authorization") String token){
        User actingUser = getActingUser(token);
        IssueResponseDTO updated = issueService.assignDeveloper(issueId, userId, actingUser);
        return ResponseEntity.ok(updated);
    }
    @PutMapping("/issues/{issueId}/status")
    public ResponseEntity<IssueResponseDTO> updateIssueStatus(@PathVariable UUID issueId, @RequestParam IssueStatus status, @RequestHeader(name = "Authorization") String token){
        User actingUser = getActingUser(token);
        IssueResponseDTO updated = issueService.updateStatus(issueId, status, actingUser);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/issues/{issueId}")
    public ResponseEntity<Void> deleteIssue(@PathVariable UUID issueId, @RequestHeader(name = "Authorization") String token){
        User actingUser = getActingUser(token);
        issueService.deleteIssue(issueId, actingUser);
        return ResponseEntity.noContent().build();
    }

    private User getActingUser(String token){
        String slicedToken = token.split(" ")[1];
        UUID actingUserId = UUID.fromString(jwtUtility.extractId(slicedToken));
        return userService.getUserByUUID(actingUserId);
    }
}
