package com.abra.revaissue.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.exception.ForbiddenOperationException;
import com.abra.revaissue.exception.UnauthenticatedException;
import com.abra.revaissue.repository.UserRepository;
import com.abra.revaissue.service.AuthzService;
import com.abra.revaissue.service.JwtService;

/**
 * LANE: Unit (FAST)
 * Tools: JUnit + Mockito
 *
 * Theater:
 * - We are not opening the theater (no Spring).
 * - We are testing one actor’s behavior in isolation.
 */
class AuthzServiceTest {

    @Test
    void actingUserId_throws_when_header_missing_or_invalid() {
        UserRepository userRepo = mock(UserRepository.class);
        JwtService jwtService = mock(JwtService.class);

        AuthzService authz = new AuthzService(userRepo, jwtService);

        assertThrows(UnauthenticatedException.class, () -> authz.actingUserId(null));
        assertThrows(UnauthenticatedException.class, () -> authz.actingUserId("NotBearer something"));
        assertThrows(UnauthenticatedException.class, () -> authz.actingUserId("Bearer")); // no space+token
    }

    @Test
    void actingUserId_returns_uuid_when_token_valid_and_not_expired() {
        UserRepository userRepo = mock(UserRepository.class);
        JwtService jwtService = mock(JwtService.class);

        AuthzService authz = new AuthzService(userRepo, jwtService);

        UUID expected = UUID.randomUUID();
        String header = "Bearer abc.def.ghi";

        // AuthzService strips "Bearer " and calls jwtService:
        when(jwtService.isTokenExpired("abc.def.ghi")).thenReturn(false);
        when(jwtService.getUserIdFromToken("abc.def.ghi")).thenReturn(expected);

        UUID actual = authz.actingUserId(header);
        assertEquals(expected, actual);
    }

    @Test
    void mustBeAdmin_throws_when_user_not_admin() {
        UserRepository userRepo = mock(UserRepository.class);
        JwtService jwtService = mock(JwtService.class);

        AuthzService authz = new AuthzService(userRepo, jwtService);

        UUID userId = UUID.randomUUID();

        User nonAdmin = new User();
        nonAdmin.setUserId(userId);
        nonAdmin.setRole(Role.TESTER);

        when(userRepo.findById(userId)).thenReturn(Optional.of(nonAdmin));

        assertThrows(ForbiddenOperationException.class, () -> authz.mustBeAdmin(userId));
    }

    @Test
    void mustBeAdmin_passes_when_user_is_admin() {
        UserRepository userRepo = mock(UserRepository.class);
        JwtService jwtService = mock(JwtService.class);

        AuthzService authz = new AuthzService(userRepo, jwtService);

        UUID userId = UUID.randomUUID();

        User admin = new User();
        admin.setUserId(userId);
        admin.setRole(Role.ADMIN);

        when(userRepo.findById(userId)).thenReturn(Optional.of(admin));

        assertDoesNotThrow(() -> authz.mustBeAdmin(userId));
    }
}
