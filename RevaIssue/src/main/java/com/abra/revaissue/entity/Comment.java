package com.abra.revaissue.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "comment_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID commentId;
    @Column
    private String message;
    @Column
    private UUID userId;
    @Column
    private UUID issueId;
    @Column
    private Date time;
}
