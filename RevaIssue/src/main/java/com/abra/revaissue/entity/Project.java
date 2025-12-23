package com.abra.revaissue.entity;

import java.time.Instant;
import java.util.UUID;

import com.abra.revaissue.enums.ProjectStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// import lombok.ToString;
// Saving for later to include exclusions if needed

@Entity
@Getter
@Setter
// @ToString
@NoArgsConstructor
@Table(name = "projects")
public class Project {
    @Id
    @Column(name = "project_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    // If UUID starts giving me trouble, I'll switch over to using @PrePersist
    private UUID projectId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "project_description")
    private String projectDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status", nullable = false)
    private ProjectStatus projectStatus = ProjectStatus.ACTIVE;

    @Column(name = "created_by_user_id", nullable = false)
    private UUID createdByUserId;

    @Column(name = "archived_by_user_id")
    private UUID archivedByUserId;

    @Column(name = "status_updated_by_user_id")
    private UUID statusUpdatedByUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "updated_at") // needs to be not null after update
    private Instant updatedAt;

}