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
/**
 * Lombok Annotation
 * Lombok composite annotation specifically = “Generate getters/setters + toString + equals/hashCode.”
 * It affects boilerplate generation. 
 * 
 * BIG NOTE/GOTCHA: equals/hashCode can cause issues with entities (especially when you add relationships).
 */
/**
 * JPA Annotation
 * Marks a persistent entity managed by Hibernate/JPA == This class is a DB record.
 * This will affect our ORM mapping. 
 * REQ: 
 * - Must have an @Id. 
 * - Needs a public or protected no-args constructor
 * 
 * NOTE: enums, records, and interfaces CAN NOT be entities
 */
/**
 * JPA Annotation
 * Table mapping override == Use this table name in the DB.
 * 
 * NOTE: table names can be confusing/reserved in some DBs; consistent naming will help.
 */
/**
 * Lombok Annotation
 * Required by JPA proxies = generate an empty constructor. See @Entity.
 * JPA will use this to instantiate entities.
 * Affects object creation.
 */
@Entity
@Getter
@Setter
// @ToString
@NoArgsConstructor
@Table(name = "projects")
public class Project {
    @Id
    /**
     * JPA Annotation
     * Identifier property == This field is the primary key.
     * Without it, app fails to start.
     * 
     * What can be a PK:
     * 1. any Java primitive type
     * 2. any primitive wrapper type:
     * - String, java.util.UUID, java.util.Date, java.sql.Date,
     * java.math.BigDecimal, java.math.BigInteger
     */
    /**
     * JPA Annotation
     * Value generation strategy == DB/framework should create the IDs for me.
     * 
     * NOTE: UUID is provider-dependent. Since we're using POSTGres later, it
     * supports UUIDs.
     */
    /**
     * JPA Annotation
     * Column mapping + DDL constraints = This is a DB column.
     * 
     * 
     * NOTE: @Column by itself on top of a field means "make a column for this
     * field". It will be nullable by default.
     * 
     * NOTE: nullable = false does not validate automatically unless schema is
     * generated + DB enforces.
     */

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