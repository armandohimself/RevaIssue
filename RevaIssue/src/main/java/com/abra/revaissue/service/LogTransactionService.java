package com.abra.revaissue.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
    public void logAction(String message, User user, EntityType entityType, UUID entityId) {
        LogTransaction log = new LogTransaction();
        log.setMessage(message);
        log.setUser(user);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        logTransactionRepository.save(log);
    }
    
}
