package com.abra.revaissue.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abra.revaissue.entity.LogTransaction;
import com.abra.revaissue.enums.EntityType;

@Repository
public interface LogTransactionRepository extends JpaRepository<LogTransaction, Long>{
    Page<LogTransaction> findByAffectedEntityTypeAndAffectedEntityId(
        EntityType entityType,
        UUID entityId,
        Pageable pageable
    );
}
