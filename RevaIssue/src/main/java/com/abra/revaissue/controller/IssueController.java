package com.abra.revaissue.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class IssueController {
    @Autowired
    private IssueService issueService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private JwtUtility jwtUtility;

    @PostMapping("/projects/{projectId}/issues")
    public Issue createIssue(@PathVariable UUID projectId, @RequestBody Issue issue, @RequestHeader(name = "Authorization") String token){
        String slicedToken = token.split(" ")[1];
        UUID actingUserId = UUID.fromString(jwtUtility.extractId(slicedToken));
        User actingUser = userService.getUserByUUID(actingUserId);
        Project project = projectService.getProjectById(projectId);
        return issueService.createIssue(issue, project, actingUser);
    }
    @GetMapping("/projects/{projectId}/issues")
    public List<Issue> getIssuesForProject(@PathVariable UUID projectId,
                                           @RequestParam(required = false)IssueStatus status,
                                           @RequestParam(required = false) IssueSeverity severity,
                                           @RequestParam(required = false)IssuePriority priority){
        Project project = projectService.getProjectById(projectId);
        if(status != null){
            return  issueService.getIssuesByProjectAndStatus(project, status);
        }
        if(severity != null){
            return issueService.getIssuesByProjectAndSeverity(project, severity);
        }
        if(priority != null){
            return issueService.getIssuesByProjectAndPriority(project, priority);
        }
        return issueService.getIssuesByProject(project);
    }
    @GetMapping("/issues/{issueId}")
    public Issue getIssueById(@PathVariable UUID issueId){
        return issueService.getIssueById(issueId);
    }
    @GetMapping("/users/{userId}/assigned-issues")
    public List<Issue> getIssuesAssignedToUser(@PathVariable UUID userId){
        User user = userService.getUserByUUID(userId);
        return issueService.getIssuesAssignedToUser(user);
    }
    @GetMapping("/users/{userId}/created-issues")
    public List<Issue> getIssuesCreatedByUser(@PathVariable UUID userId){
        User user = userService.getUserByUUID(userId);
        return issueService.getIssuesCreatedByUser(user);
    }
    @PutMapping("/issues/{issueId}/assign/{userId}")
    public Issue assignDeveloper(@PathVariable UUID issueId, @PathVariable UUID userId, @RequestHeader(name = "Authorization") String token){
        String slicedToken = token.split(" ")[1];
        UUID actingUserId = UUID.fromString(jwtUtility.extractId(slicedToken));
        User actingUser = userService.getUserByUUID(actingUserId);
        Issue issue = issueService.getIssueById(issueId);
        User user = userService.getUserByUUID(userId);
        return issueService.assignDeveloper(issue, user);
    }
    @PutMapping("/issues/{issueId}/status")
    public Issue updateIssueStatus(@PathVariable UUID issueId, @RequestParam IssueStatus status, @RequestHeader(name = "Authorization") String token){
        String slicedToken = token.split(" ")[1];
        UUID actingUserId = UUID.fromString(jwtUtility.extractId(slicedToken));
        User actingUser = userService.getUserByUUID(actingUserId);
        Issue issue = issueService.getIssueById(issueId);
        return issueService.updateStatus(issue, status, actingUser);
    }
    @DeleteMapping("/{issueId")
    public ResponseEntity<?> deleteIssue(@PathVariable UUID issueId, @RequestHeader(name = "Authorization") String token){
        String slicedToken = token.split(" ")[1];
        UUID actingUserId = UUID.fromString(jwtUtility.extractId(slicedToken));
        User actingUser = userService.getUserByUUID(actingUserId);
        issueService.deleteIssue(issueId, actingUser);
        return ResponseEntity.noContent().build();
    }
}
