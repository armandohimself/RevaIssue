package com.abra.revaissue.entity;

import java.time.Instant;
import java.util.UUID;

import com.abra.revaissue.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.abra.revaissue.enums.EntityType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "log_transaction")
public class LogTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(nullable = false)
    private String message;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User actingUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType affectedEntityType;

    @Column(nullable = false)
    private UUID affectedEntityId;

    @Column(nullable = false)
    private Instant date;
}
