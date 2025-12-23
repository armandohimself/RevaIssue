package com.abra.revaissue.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Lombok Annotation
 * Lombok composite annotation specifically = “Generate getters/setters + toString + equals/hashCode.”
 * It affects boilerplate generation. 
 * 
 * BIG NOTE/GOTCHA: equals/hashCode can cause issues with entities (especially when you add relationships).
 */
@Data
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
@Entity
/**
 * JPA Annotation
 * Table mapping override == Use this table name in the DB.
 * 
 * NOTE: table names can be confusing/reserved in some DBs; consistent naming will help.
 */
@Table(name = "project")
/**
 * Lombok Annotation
 * Required by JPA proxies = generate an empty constructor. See @Entity.
 * JPA will use this to instantiate entities.
 * Affects object creation. 
 */
@NoArgsConstructor
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
     *  - String, java.util.UUID, java.util.Date, java.sql.Date, java.math.BigDecimal, java.math.BigInteger
     */
    @GeneratedValue(strategy = GenerationType.UUID)
    /**
     * JPA Annotation
     * Value generation strategy == DB/framework should create the IDs for me. 
     * 
     * NOTE: UUID is provider-dependent. Since we're using POSTGres later, it supports UUIDs.
     */
    @Column(name = "project_id", nullable = false)
    /**
     * JPA Annotation
     * Column mapping + DDL constraints = This is a DB column. 
     * 
     * 
     * NOTE: @Column by itself on top of a field means "make a column for this field". It will be nullable by default. 
     * 
     * NOTE: nullable = false does not validate automatically unless schema is generated + DB enforces.
     */
    private UUID projectId;
    @Column
    private String projectName;
}