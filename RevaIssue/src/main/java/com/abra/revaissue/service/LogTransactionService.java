package com.abra.revaissue.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.abra.revaissue.entity.LogTransaction;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.repository.LogTransactionRepository;
import com.abra.revaissue.enums.EntityType;

@Service
public class LogTransactionService {
    
    @Autowired
    private LogTransactionRepository logTransactionRepository;

    // log the transaction
    public void logAction(String message, User actingUser, EntityType affectedEntityType, UUID affectedEntityId) {
        LogTransaction log = new LogTransaction();
        log.setMessage(message);
        log.setActingUser(actingUser);
        log.setAffectedEntityType(affectedEntityType);
        log.setAffectedEntityId(affectedEntityId);
        log.setDate(Instant.now());
        logTransactionRepository.save(log);
    }

    public Page<LogTransaction> getAllLogs(Pageable pageable) {
        return logTransactionRepository.findAll(pageable);
    }

    public Page<LogTransaction> getLogsByEntityTypeAndId(
        EntityType entityType,
        UUID entityId,
        Pageable pageable
    ) {
        return logTransactionRepository.findByAffectedEntityTypeAndAffectedEntityId(
            entityType,
            entityId,
            pageable
        );
    }
}
