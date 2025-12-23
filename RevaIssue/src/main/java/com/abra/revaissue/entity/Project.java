package com.abra.revaissue.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @Column(name = "project_id")
    private UUID projectId;
    @Column(name="project_name")
    private String projectName;
    @Column(name = "project_description")
    private String projectDescription;
    // private enum projectStatus;
    // private interface of members: projectMembers[]
    @Column(name = "created_by_user_id")
    private UUID createdByUserId;
    @Column(name = "archived_by_user_id")
    private UUID archivedByUserId;
    @Column(name = "status_updated_by_user_id")
    private UUID statusUpdatedByUserId;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "archived_at")
    private Instant archivedAt;
    @Column(name = "updated_at")
    private Instant updatedAt;

}