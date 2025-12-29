package com.abra.revaissue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abra.revaissue.entity.LogTransaction;

@Repository
public interface LogTransactionRepository extends JpaRepository<LogTransaction, Long>{
    
}
