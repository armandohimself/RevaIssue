package com.abra.revaissue.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.abra.revaissue.entity.LogTransaction;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.EntityType;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.LogTransactionRepository;
import com.abra.revaissue.service.LogTransactionService;

@ExtendWith(MockitoExtension.class)
public class LogTransactionServiceTest {

    @Mock
    private LogTransactionRepository logTransactionRepository;

    @InjectMocks
    private LogTransactionService logTransactionService;

    private User testUser;
    private UUID testEntityId;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(UUID.randomUUID());
        testUser.setUserName("testuser");
        testUser.setRole(Role.ADMIN);

        testEntityId = UUID.randomUUID();
    }

    @Test
    void testLogAction_Success() {
        // arrange
        String message = "User created a new issue";
        EntityType entityType = EntityType.ISSUE;

        when(logTransactionRepository.save(any(LogTransaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // act
        logTransactionService.logAction(message, testUser, entityType, testEntityId);

        // assert
        ArgumentCaptor<LogTransaction> logCaptor = ArgumentCaptor.forClass(LogTransaction.class);
        verify(logTransactionRepository, times(1)).save(logCaptor.capture());

        LogTransaction capturedLog = logCaptor.getValue();
        assertEquals(message, capturedLog.getMessage());
        assertEquals(testUser, capturedLog.getActingUser());
        assertEquals(entityType, capturedLog.getAffectedEntityType());
        assertEquals(testEntityId, capturedLog.getAffectedEntityId());
        assertNotNull(capturedLog.getDate());
        assertTrue(capturedLog.getDate().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    void testLogAction_WithDifferentEntityTypes() {
        // arrange
        String message = "User updated";
        EntityType entityType = EntityType.USER;

        when(logTransactionRepository.save(any(LogTransaction.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // act
        logTransactionService.logAction(message, testUser, entityType, testEntityId);

        // assert
        ArgumentCaptor<LogTransaction> logCaptor = ArgumentCaptor.forClass(LogTransaction.class);
        verify(logTransactionRepository).save(logCaptor.capture());

        LogTransaction capturedLog = logCaptor.getValue();
        assertEquals(EntityType.USER, capturedLog.getAffectedEntityType());
    }

    @Test
    void testGetAllLogs_Success() {
        // arrange
        Pageable pageable = PageRequest.of(0, 20);
        List<LogTransaction> logList = createTestLogs(3);
        Page<LogTransaction> expectedPage = new PageImpl<>(logList, pageable, logList.size());

        when(logTransactionRepository.findAll(pageable)).thenReturn(expectedPage);

        // act
        Page<LogTransaction> result = logTransactionService.getAllLogs(pageable);

        // assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        verify(logTransactionRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllLogs_EmptyResult() {
        // arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<LogTransaction> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(logTransactionRepository.findAll(pageable)).thenReturn(emptyPage);

        // act
        Page<LogTransaction> result = logTransactionService.getAllLogs(pageable);

        // assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(logTransactionRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllLogs_WithPagination() {
        // arrange
        Pageable firstPage = PageRequest.of(0, 10);
        List<LogTransaction> firstPageLogs = createTestLogs(10);
        Page<LogTransaction> firstPageResult = new PageImpl<>(firstPageLogs, firstPage, 25);

        when(logTransactionRepository.findAll(firstPage)).thenReturn(firstPageResult);

        // act
        Page<LogTransaction> result = logTransactionService.getAllLogs(firstPage);

        // assert
        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(25, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertTrue(result.hasNext());
        assertFalse(result.hasPrevious());
    }

    @Test
    void testGetLogsByEntityTypeAndId_Success() {
        // arrange
        EntityType entityType = EntityType.ISSUE;
        UUID entityId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);

        List<LogTransaction> logList = createTestLogsForEntity(entityType, entityId, 5);
        Page<LogTransaction> expectedPage = new PageImpl<>(logList, pageable, logList.size());

        when(logTransactionRepository.findByAffectedEntityTypeAndAffectedEntityId(
            entityType, entityId, pageable)).thenReturn(expectedPage);

        // act
        Page<LogTransaction> result = logTransactionService.getLogsByEntityTypeAndId(
            entityType, entityId, pageable);

        // assert
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        result.getContent().forEach(log -> {
            assertEquals(entityType, log.getAffectedEntityType());
            assertEquals(entityId, log.getAffectedEntityId());
        });
        verify(logTransactionRepository, times(1))
            .findByAffectedEntityTypeAndAffectedEntityId(entityType, entityId, pageable);
    }

    @Test
    void testGetLogsByEntityTypeAndId_EmptyResult() {
        // arrange
        EntityType entityType = EntityType.PROJECT;
        UUID entityId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);
        Page<LogTransaction> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(logTransactionRepository.findByAffectedEntityTypeAndAffectedEntityId(
            entityType, entityId, pageable)).thenReturn(emptyPage);

        // act
        Page<LogTransaction> result = logTransactionService.getLogsByEntityTypeAndId(
            entityType, entityId, pageable);

        // assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testGetLogsByEntityTypeAndId_MultipleEntityTypes() {
        // arrange
        UUID issueId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);

        List<LogTransaction> issueLogs = createTestLogsForEntity(EntityType.ISSUE, issueId, 3);
        Page<LogTransaction> issuePage = new PageImpl<>(issueLogs, pageable, issueLogs.size());

        when(logTransactionRepository.findByAffectedEntityTypeAndAffectedEntityId(
            EntityType.ISSUE, issueId, pageable)).thenReturn(issuePage);

        // act
        Page<LogTransaction> result = logTransactionService.getLogsByEntityTypeAndId(
            EntityType.ISSUE, issueId, pageable);

        // assert
        assertEquals(3, result.getContent().size());
        result.getContent().forEach(log -> 
            assertEquals(EntityType.ISSUE, log.getAffectedEntityType())
        );
    }

    private List<LogTransaction> createTestLogs(int count) {
        List<LogTransaction> logs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LogTransaction log = new LogTransaction();
            log.setLogId((long) i);
            log.setMessage("Test log message " + i);
            log.setActingUser(testUser);
            log.setAffectedEntityType(EntityType.ISSUE);
            log.setAffectedEntityId(UUID.randomUUID());
            log.setDate(Instant.now().minusSeconds(i * 60));
            logs.add(log);
        }
        return logs;
    }

    private List<LogTransaction> createTestLogsForEntity(
        EntityType entityType, UUID entityId, int count) {
        List<LogTransaction> logs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            LogTransaction log = new LogTransaction();
            log.setLogId((long) i);
            log.setMessage("Log for entity " + i);
            log.setActingUser(testUser);
            log.setAffectedEntityType(entityType);
            log.setAffectedEntityId(entityId);
            log.setDate(Instant.now().minusSeconds(i * 60));
            logs.add(log);
        }
        return logs;
    }
}
