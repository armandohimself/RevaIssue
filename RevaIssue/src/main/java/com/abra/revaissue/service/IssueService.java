package com.abra.revaissue.service;

import com.abra.revaissue.dto.IssueCreateDTO;
import com.abra.revaissue.dto.IssueResponseDTO;
import com.abra.revaissue.dto.IssueUpdateDTO;
import com.abra.revaissue.entity.Issue;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.*;
import com.abra.revaissue.exception.UnauthorizedOperation;
import com.abra.revaissue.repository.IssueRepository;
import com.abra.revaissue.util.IssueMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final IssueMapper issueMapper;
    private final UserService userService;
    private final LogTransactionService logTransactionService;

    public IssueService(IssueRepository issueRepository, IssueMapper issueMapper, UserService userService, LogTransactionService logTransactionService){
        this.issueRepository = issueRepository;
        this.issueMapper = issueMapper;
        this.userService = userService;
        this.logTransactionService = logTransactionService;
    }
    //CREATE
    public IssueResponseDTO createIssue(IssueCreateDTO dto, Project project, User actingUser){
        UserEnum.Role role = actingUser.getRole();
        if(role != UserEnum.Role.TESTER && role != UserEnum.Role.ADMIN){
            throw new UnauthorizedOperation("Only TESTERS OR ADMINS can create issues");
        }
        Issue issue = issueMapper.toIssue(dto);
        issue.setProject(project);
        issue.setCreatedBy(actingUser);
        issue.setStatus(IssueStatus.OPEN);
        issue.setCreatedAt(Instant.now());
        issue.setUpdatedAt(Instant.now());
        Issue created = issueRepository.save(issue);
        String logMessage = actingUser.getUserName() + " created issue: " + issue.getName();
        logTransactionService.logAction(logMessage, actingUser, EntityType.ISSUE, issue.getIssueId());
        return issueMapper.toResponseDTO(created);
    }
    //SELECT/QUERIES
    public List<IssueResponseDTO> getIssuesByProject(Project project){
        return issueRepository.findByProject_ProjectId(project.getProjectId())
                .stream()
                .map(issueMapper::toResponseDTO)
                .toList();
    }
    public List<IssueResponseDTO> getIssuesByProjectAndStatus(Project project, IssueStatus status){
        return issueRepository.findByProject_ProjectIdAndStatus(project.getProjectId(), status)
                .stream()
                .map(issueMapper::toResponseDTO)
                .toList();
    }
    public List<IssueResponseDTO> getIssuesByProjectAndSeverity(Project project, IssueSeverity severity){
        return issueRepository.findByProject_ProjectIdAndSeverity(project.getProjectId(), severity)
                .stream()
                .map(issueMapper::toResponseDTO)
                .toList();
    }
    public List<IssueResponseDTO> getIssuesByProjectAndPriority(Project project, IssuePriority priority){
        return issueRepository.findByProject_ProjectIdAndPriority(project.getProjectId(), priority)
                .stream()
                .map(issueMapper::toResponseDTO)
                .toList();
    }
    public List<IssueResponseDTO> getIssuesCreatedByUser(User user){
        return issueRepository.findByCreatedBy_UserId(user.getUserId())
                .stream()
                .map(issueMapper::toResponseDTO)
                .toList();
    }
    public List<IssueResponseDTO> getIssuesAssignedToUser(User user){
        return issueRepository.findByAssignedTo_UserId(user.getUserId())
                .stream()
                .map(issueMapper::toResponseDTO)
                .toList();
    }
    public List<IssueResponseDTO> getIssuesByProjectAndAssignedToUser(Project project, User user){
        return issueRepository.findByProject_ProjectIdAndAssignedTo_UserId(project.getProjectId(), user.getUserId())
                .stream()
                .map(issueMapper::toResponseDTO)
                .toList();
    }
    public List<IssueResponseDTO> getIssuesByProjectAndCreatedByUser(Project project, User user){
        return issueRepository.findByProject_ProjectIdAndCreatedBy_UserId(project.getProjectId(), user.getUserId())
                .stream()
                .map(issueMapper::toResponseDTO)
                .toList();
    }
    public IssueResponseDTO getIssueById(UUID issueId){
        Issue issue =  issueRepository.findById(issueId).orElseThrow(() -> new EntityNotFoundException("Issue not found"));
        return issueMapper.toResponseDTO(issue);
    }
    //UPDATE
    public IssueResponseDTO updateIssue(UUID issueId, IssueUpdateDTO dto, User actingUser){
        UserEnum.Role role = actingUser.getRole();
        if(role != UserEnum.Role.TESTER && role != UserEnum.Role.ADMIN){
            throw new UnauthorizedOperation("Only TESTERS OR ADMINS can update issue details");
        }
        Issue issue =  issueRepository.findById(issueId).orElseThrow(() -> new EntityNotFoundException("Issue not found"));
        issueMapper.updateEntity(dto, issue);
        issue.setUpdatedAt(Instant.now());
        String logMessage = actingUser.getUserName() + " updated issue: " + issue.getName();
        logTransactionService.logAction(logMessage, actingUser, EntityType.ISSUE, issue.getIssueId());
        return issueMapper.toResponseDTO(issueRepository.save(issue));
    }
    public IssueResponseDTO assignDeveloper(UUID issueId, UUID userId, User actingUser){
        UserEnum.Role role = actingUser.getRole();
        if(role != UserEnum.Role.TESTER && role != UserEnum.Role.ADMIN){
            throw new UnauthorizedOperation("Only TESTERS OR ADMINS can assign issues");
        }
        User assignedUser = userService.getUserByUUID(userId);
        if(assignedUser.getRole() != UserEnum.Role.DEVELOPER){
            throw new UnauthorizedOperation("Only users with DEVELOPER role can be assigned");
        }
        Issue issue =  issueRepository.findById(issueId).orElseThrow(() -> new EntityNotFoundException("Issue not found"));
        issue.setUpdatedAt(Instant.now());
        issue.setAssignedTo(assignedUser);
        String logMessage = actingUser.getUserName() + " assigned " + assignedUser.getUserName() + " to " + issue.getName();
        logTransactionService.logAction(logMessage, actingUser, EntityType.ISSUE, issue.getIssueId());
        return issueMapper.toResponseDTO(issueRepository.save(issue));
    }
    public IssueResponseDTO updateStatus(UUID issueId, IssueStatus status, User actingUser){
        Issue issue =  issueRepository.findById(issueId).orElseThrow(() -> new EntityNotFoundException("Issue not found"));
        UserEnum.Role role = actingUser.getRole();
        switch (role) {
            case DEVELOPER -> {
                if(status != IssueStatus.IN_PROGRESS && status != IssueStatus.RESOLVED){
                    throw new UnauthorizedOperation("Developers can only move issues to in progress or resolved");
                }
            }
            case TESTER -> {
                if(status != IssueStatus.OPEN && status != IssueStatus.CLOSED){
                    throw new UnauthorizedOperation("Testers can only move issues to open or closed");
                }
            }
            case ADMIN -> {}
            default -> throw new UnauthorizedOperation("Role not permitted to change issue status");
        }
        String logMessage = actingUser.getUserName() + " updated issue status from " + issue.getStatus() + " to " + status;
        issue.setStatus(status);
        issue.setUpdatedAt(Instant.now());
        logTransactionService.logAction(logMessage, actingUser, EntityType.ISSUE, issue.getIssueId());
        return issueMapper.toResponseDTO(issueRepository.save(issue));
    }
    //DELETE
    public void deleteIssue(UUID issueId, User actingUser){
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new EntityNotFoundException("Issue not found"));
        UserEnum.Role role = actingUser.getRole();
        if(role != UserEnum.Role.TESTER && role != UserEnum.Role.ADMIN){
            throw new UnauthorizedOperation("Only TESTERS OR ADMINS can delete issues");
        }
        String logMessage = actingUser.getUserName() + " deleted issue: " + issue.getName();
        logTransactionService.logAction(logMessage, actingUser, EntityType.ISSUE, issue.getIssueId());
        issueRepository.delete(issue);
    }
    public Issue getIssueEntityById(UUID issueId) {
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found"));
    }
}
