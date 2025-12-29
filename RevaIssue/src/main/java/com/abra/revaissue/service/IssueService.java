package com.abra.revaissue.service;

import com.abra.revaissue.entity.Issue;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import com.abra.revaissue.enums.IssueStatus;
import com.abra.revaissue.enums.UserEnum;
import com.abra.revaissue.repository.IssueRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IssueService {

    private final IssueRepository issueRepository;

    @Autowired
    public IssueService(IssueRepository issueRepository){
        this.issueRepository = issueRepository;
    }
    //CREATE
    public Issue createIssue(Issue issue, Project project, User actingUser){
        UserEnum.Role role = actingUser.getRole();
        if(role != UserEnum.Role.TESTER && role != UserEnum.Role.ADMIN){
            throw new RuntimeException("Only TESTERS OR ADMINS can create issues");
        }
        issue.setCreatedAt(Instant.now());
        issue.setUpdatedAt(Instant.now());
        issue.setProject(project);
        return issueRepository.save(issue);
    }
    //SELECT/QUERIES
    public List<Issue> getIssuesByProject(Project project){
        return issueRepository.findByProject_ProjectId(project.getProjectId());
    }
    public List<Issue> getIssuesByProjectAndStatus(Project project, IssueStatus status){
        return issueRepository.findByProject_ProjectIdAndStatus(project.getProjectId(), status);
    }
    public List<Issue> getIssuesByProjectAndSeverity(Project project, IssueSeverity severity){
        return issueRepository.findByProject_ProjectIdAndSeverity(project.getProjectId(), severity);
    }
    public List<Issue> getIssuesByProjectAndPriority(Project project, IssuePriority priority){
        return issueRepository.findByProject_ProjectIdAndPriority(project.getProjectId(), priority);
    }
    public List<Issue> getIssuesCreatedByUser(User user){
        return issueRepository.findByCreatedBy_UserId(user.getUserId());
    }
    public List<Issue> getIssuesAssignedToUser(User user){
        return issueRepository.findByAssignedTo_UserId(user.getUserId());
    }
    public List<Issue> getIssuesByProjectAndAssignedToUser(Project project, User user){
        return issueRepository.findByProject_ProjectIdAndAssignedTo_UserId(project.getProjectId(), user.getUserId());
    }
    public List<Issue> getIssuesByProjectAndCreatedByUser(Project project, User user){
        return issueRepository.findByProject_ProjectIdAndCreatedBy_UserId(project.getProjectId(), user.getUserId());
    }
    public Issue getIssueById(UUID issueId){
        return issueRepository.findById(issueId).orElseThrow(() -> new EntityNotFoundException("Issue not found with id: " + issueId));
    }
    //UPDATE
    public Issue assignDeveloper(Issue issue, User actingUser){
        UserEnum.Role role = actingUser.getRole();
        if(role != UserEnum.Role.TESTER && role != UserEnum.Role.ADMIN){
            throw new RuntimeException("Only TESTERS OR ADMINS can assign issues");
        }
        issue.setUpdatedAt(Instant.now());
        issue.setAssignedTo(user);
        return issueRepository.save(issue);
    }
    public Issue updateStatus(Issue issue, IssueStatus status, User actingUser){
        UserEnum.Role role = actingUser.getRole();
        if(role != UserEnum.Role.TESTER && role != UserEnum.Role.ADMIN){
            throw new RuntimeException("Only TESTERS OR ADMINS can set issue status");
        }
        issue.setStatus(status);
        issue.setUpdatedAt(Instant.now());
        return issueRepository.save(issue);
    }
    //DELETE
    public void deleteIssue(UUID issueId, User actingUser){
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new EntityNotFoundException("Issue not found"));
        UserEnum.Role role = actingUser.getRole();
        if(role != UserEnum.Role.TESTER && role != UserEnum.Role.ADMIN){
            throw new RuntimeException("Only TESTERS OR ADMINS can delete issues");
        }
        issueRepository.delete(issue);
    }
}
