package com.abra.revaissue.repository;

import com.abra.revaissue.entity.Issue;
import com.abra.revaissue.entity.Project;
import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import com.abra.revaissue.enums.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID> {
    //All issues for a project
    List<Issue> findByProject_ProjectId(UUID projectId);
    //All issues for a project with a certain status
    List<Issue> findByProject_ProjectIdAndStatus(UUID projectId, IssueStatus status);
    //All issues for a project with a certain severity
    List<Issue> findByProject_ProjectIdAndSeverity(UUID projectId, IssueSeverity severity);
    //All issues for a project with a certain priority
    List<Issue> findByProject_ProjectIdAndPriority(UUID projectId, IssuePriority priority);
    //All issues made by a user
    List<Issue> findByCreatedBy_UserId(UUID userId);
    //All issues assigned to a user
    List<Issue> findByAssignedTo_UserId(UUID userId);
    //All issues assigned to a user for a certain project
    List<Issue> findByProject_ProjectIdAndAssignedTo_UserId(UUID projectId, UUID userId);
    //All issues created by a user for a certain project
    List<Issue> findByProject_ProjectIdAndCreatedBy_UserId(UUID projectId, UUID userId);
}
