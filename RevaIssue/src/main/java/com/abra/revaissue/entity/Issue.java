package com.abra.revaissue.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "issue")
public class Issue {

    @Id
    @Column(name = "issue_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID issueId;
    @Column
    private String name;
    @Column
    private String description;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project projectId;


}
