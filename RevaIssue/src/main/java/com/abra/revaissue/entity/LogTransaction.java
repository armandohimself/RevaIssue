package com.abra.revaissue.entity;

import java.time.Instant;
import java.util.UUID;

import com.abra.revaissue.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "log_transaction")
public class LogTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID logId;

    @Column(nullable = false)
    private String message;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String entityType; // "Project" / "Issue"

    @Column(nullable = false)
    private UUID entityId;

    @Column(nullable = false)
    private Instant date;
}
