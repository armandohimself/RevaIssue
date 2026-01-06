package com.abra.revaissue.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abra.revaissue.service.AuthzService;
import com.abra.revaissue.service.LogTransactionService;

import com.abra.revaissue.entity.LogTransaction;
import com.abra.revaissue.enums.EntityType;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/logs")
public class LogTransactionController {
    private LogTransactionService logTransactionService;
    private AuthzService authzService;

    public LogTransactionController(
        LogTransactionService logTransactionService,
        AuthzService authzService
    ) {
        this.logTransactionService = logTransactionService;
        this.authzService = authzService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<Page<LogTransaction>> getAllLogs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestHeader(name = "Authorization") String token
    ) {
        UUID actingUserId = authzService.actingUserId(token);
        authzService.mustBeAdmin(actingUserId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<LogTransaction> logs = logTransactionService.getAllLogs(pageable);

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/issue/{issueId}")
    public ResponseEntity<Page<LogTransaction>> getIssueHistory(
        @PathVariable UUID issueId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestHeader(name = "Authorization") String token
    ) {
        authzService.actingUserId(token);

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<LogTransaction> logs = logTransactionService.getLogsByEntityTypeAndId(
            EntityType.ISSUE,
            issueId,
            pageable
        );
        return ResponseEntity.ok(logs);
    }
}
