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
    /**Returns all issues for a project*/
    List<Issue> findByProject_ProjectId(UUID projectId);
    /**Returns all issues for a project with a specified status*/
    List<Issue> findByProject_ProjectIdAndStatus(UUID projectId, IssueStatus status);
    /**Returns all issues for a project with a specified severity*/
    List<Issue> findByProject_ProjectIdAndSeverity(UUID projectId, IssueSeverity severity);
    /**Returns all issues for a project with a specified priority*/
    List<Issue> findByProject_ProjectIdAndPriority(UUID projectId, IssuePriority priority);
    /**Returns all issues made by a user*/
    List<Issue> findByCreatedBy_UserId(UUID userId);
    /**Returns all issues assigned to a user*/
    List<Issue> findByAssignedTo_UserId(UUID userId);
    /**Returns all issues assigned to a user for a specified project*/
    List<Issue> findByProject_ProjectIdAndAssignedTo_UserId(UUID projectId, UUID userId);
    /**Returns all issues created by a user for a specified project*/
    List<Issue> findByProject_ProjectIdAndCreatedBy_UserId(UUID projectId, UUID userId);
}
