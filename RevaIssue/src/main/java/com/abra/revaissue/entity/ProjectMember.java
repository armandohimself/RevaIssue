package com.abra.revaissue.entity;

import java.time.Instant;
import java.util.UUID;

import com.abra.revaissue.enums.ProjectRole;

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

@Entity
@Table(name = "project_members")
@Getter
@Setter
@NoArgsConstructor
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "project_member_id", nullable = false)
    private UUID projectMemberId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId; // which project are we pointing to?

    @Column(name = "user_id", nullable = false)
    private UUID userId; // which user was added to this project by id?

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role", nullable = false)
    private ProjectRole projectRole; // what is the user allowed to do in this project?

    @Column(name = "assigned_by_user_id", nullable = false)
    private UUID assignedByUserId;

    @Column(name = "member_assigned_at", nullable = false)
    private Instant memberAssignedAt;

    @Column(name = "removed_by_user_id")
    private UUID removedByUserId;

    @Column(name = "removed_at")
    private Instant removedAt; // null means still active in project
}
