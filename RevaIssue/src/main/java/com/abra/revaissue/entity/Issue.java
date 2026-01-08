package com.abra.revaissue.entity;

import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.IssuePriority;
import com.abra.revaissue.enums.IssueSeverity;
import com.abra.revaissue.enums.IssueStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @Column(name = "issue_id", nullable=false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID issueId;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_status",nullable = false)
    private IssueStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_severity", nullable = false)
    private IssueSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "issue_priority", nullable = false)
    private IssuePriority priority;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedTo;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
