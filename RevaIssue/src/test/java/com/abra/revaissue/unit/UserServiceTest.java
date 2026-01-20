package com.abra.revaissue.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.abra.revaissue.dto.CreateUserDTO;
import com.abra.revaissue.dto.LoginRequestDTO;
import com.abra.revaissue.dto.TokenTransport;
import com.abra.revaissue.dto.UpdateUserDTO;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.UserRepository;
import com.abra.revaissue.service.JwtService;
import com.abra.revaissue.service.LogTransactionService;
import com.abra.revaissue.service.UserService;

public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private LogTransactionService logTransactionService;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    
    private UserService userService;
    private UUID testUserId;
    private User testUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(
        userRepository,
        logTransactionService,
        jwtService,
        passwordEncoder
    );
        
        testUserId = UUID.randomUUID();
        testUser = new User(testUserId, "admin", "hashedPassword123", Role.ADMIN);
    }
    
    // login tests
    
    @Test
    void testLoginSuccess_ValidCredentials() {
        // arrange
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUserName("admin");
        requestDTO.setPassword("password");

        User mockUser = new User();
        mockUser.setUserId(UUID.randomUUID());
        mockUser.setUserName("admin");
        mockUser.setPasswordHash("hashedPassword123");
        mockUser.setRole(Role.ADMIN);

        when(userRepository.findByUserName("admin")).thenReturn(mockUser);
        when(passwordEncoder.matches("password", "hashedPassword123")).thenReturn(true);
        when(jwtService.createToken(mockUser.getUserId(), "admin", Role.ADMIN)).thenReturn("valid.jwt.token");

        // act
        TokenTransport token = userService.login(requestDTO);

        // assert
        assertNotNull(token);
        assertNotNull(token.getToken());
        assertFalse(token.getToken().isBlank());
        assertEquals("valid.jwt.token", token.getToken());
        verify(userRepository, times(1)).findByUserName("admin");
    }
    
    @Test
    void testLoginFailure_InvalidCredentials() {
        // arrange
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUserName("admin");
        requestDTO.setPassword("wrongpassword");

        User mockUser = new User();
        mockUser.setUserId(UUID.randomUUID());
        mockUser.setUserName("admin");
        mockUser.setPasswordHash("hashedPassword123");
        mockUser.setRole(Role.ADMIN);

        when(userRepository.findByUserName("admin")).thenReturn(mockUser);
        when(passwordEncoder.matches("wrongpassword", "hashedPassword123")).thenReturn(false);

        // act
        TokenTransport token = userService.login(requestDTO);

        // assert
        assertNotNull(token);
        assertTrue(token.getToken() == null);
        verify(userRepository, times(1)).findByUserName("admin");
        verify(jwtService, never()).createToken(any(), any(), any());
    }
    
    @Test
    void testLoginFailure_UserNotFound() {
        // arrange
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUserName("nonexistent");
        requestDTO.setPassword("password");

        when(userRepository.findByUserName("nonexistent")).thenReturn(null);

        // act
        TokenTransport token = userService.login(requestDTO);

        // assert
        assertNull(token);
        verify(userRepository, times(1)).findByUserName("nonexistent");
        verify(jwtService, never()).createToken(any(), any(), any());
    }
    
    // create user tests
    
    @Test
    void testCreateUser_Success() {
        // arrange
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUserName("newuser");
        createUserDTO.setPassword("password");
        createUserDTO.setRole(Role.DEVELOPER);

        UUID actingUserId = UUID.randomUUID();
        User actingUser = new User(actingUserId, "admin", "hashedPassword", Role.ADMIN);

        when(userRepository.findByUserName("newuser")).thenReturn(null);
        when(userRepository.findByUserId(actingUserId)).thenReturn(actingUser);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User(UUID.randomUUID(), "newuser", "encodedPassword", Role.DEVELOPER));

        // act
        User createdUser = userService.createUser(createUserDTO, actingUserId);

        // assert
        assertNotNull(createdUser);
        assertEquals("newuser", createdUser.getUserName());
        assertEquals(Role.DEVELOPER, createdUser.getRole());
        verify(userRepository, times(1)).save(any(User.class));
        verify(logTransactionService, times(1)).logAction(any(), any(), any(), any());
    }
    
    @Test
    void testCreateUser_UsernameAlreadyTaken() {
        // arrange
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUserName("admin");
        createUserDTO.setPassword("password");
        createUserDTO.setRole(Role.DEVELOPER);

        UUID actingUserId = UUID.randomUUID();
        User existingUser = new User(UUID.randomUUID(), "admin", "hashedPassword", Role.ADMIN);

        when(userRepository.findByUserId(actingUserId)).thenReturn(testUser);
        when(userRepository.findByUserName("admin")).thenReturn(existingUser);

        // act & assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(createUserDTO, actingUserId);
        });
        
        assertEquals("Username already taken", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void testCreateUser_NullDTO() {
        // act
        User result = userService.createUser(null, UUID.randomUUID());

        // assert
        assertNull(result);
        verify(userRepository, never()).save(any());
    }
    
    // delete user tests
    
    @Test
    void testDeleteUserByUUID_Success() {
        // arrange
        UUID userToDeleteId = UUID.randomUUID();
        UUID actingUserId = UUID.randomUUID();
        
        User userToDelete = new User(userToDeleteId, "testuser", "hashedPassword", Role.DEVELOPER);
        User actingUser = new User(actingUserId, "admin", "hashedPassword", Role.ADMIN);

        when(userRepository.findByUserId(userToDeleteId)).thenReturn(userToDelete);
        when(userRepository.findByUserId(actingUserId)).thenReturn(actingUser);
        when(userRepository.existsByUserId(userToDeleteId)).thenReturn(false);

        // act
        boolean result = userService.deleteUserByUUID(userToDeleteId, actingUserId);

        // assert
        assertTrue(result);
        verify(userRepository, times(1)).delete(userToDelete);
        verify(logTransactionService, times(1)).logAction(any(), any(), any(), any());
    }
    
    @Test
    void testDeleteUserByUUID_UserNotFound() {
        // arrange
        UUID userToDeleteId = UUID.randomUUID();
        UUID actingUserId = UUID.randomUUID();

        when(userRepository.findByUserId(userToDeleteId)).thenReturn(null);

        // act
        boolean result = userService.deleteUserByUUID(userToDeleteId, actingUserId);

        // assert
        assertFalse(result);
        verify(userRepository, never()).delete(any());
    }
    
    // get user test
    
    @Test
    void testGetUserByUUID_Success() {
        // arrange
        when(userRepository.findByUserId(testUserId)).thenReturn(testUser);

        // act
        User result = userService.getUserByUUID(testUserId);

        // assert
        assertNotNull(result);
        assertEquals("admin", result.getUserName());
        assertEquals(Role.ADMIN, result.getRole());
        verify(userRepository, times(2)).findByUserId(testUserId);
    }
    
    @Test
    void testGetUserByUUID_NotFound() {
        // arrange
        UUID nonexistentId = UUID.randomUUID();
        when(userRepository.findByUserId(nonexistentId)).thenReturn(null);

        // act & assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserByUUID(nonexistentId);
        });
        
        assertEquals("User not found", exception.getMessage());
    }
    
    @Test
    void testGetAllUsers() {
        // arrange
        User user1 = new User(UUID.randomUUID(), "user1", "hash1", Role.DEVELOPER);
        User user2 = new User(UUID.randomUUID(), "user2", "hash2", Role.TESTER);
        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // act
        List<User> result = userService.getAllUsers();

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }
    
    // get users by role tests
    
    @Test
    void testGetUsersByRole() {
        // arrange
        User admin1 = new User(UUID.randomUUID(), "admin1", "hash1", Role.ADMIN);
        User admin2 = new User(UUID.randomUUID(), "admin2", "hash2", Role.ADMIN);
        List<User> admins = List.of(admin1, admin2);

        when(userRepository.findAllByRole(Role.ADMIN)).thenReturn(admins);

        // act
        List<User> result = userService.getUsersByRole(Role.ADMIN);

        // assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(u -> u.getRole() == Role.ADMIN));
        verify(userRepository, times(1)).findAllByRole(Role.ADMIN);
    }
    
    // update user tests
    
    @Test
    void testUpdateUserByUUID_Success() {
        // arrange
        UUID userToUpdateId = UUID.randomUUID();
        UUID actingUserId = UUID.randomUUID();
        
        User existingUser = new User(userToUpdateId, "oldname", "hashedPassword", Role.DEVELOPER);
        User actingUser = new User(actingUserId, "admin", "hashedPassword", Role.ADMIN);

        UpdateUserDTO updateDTO = new UpdateUserDTO();
        updateDTO.setUserId(userToUpdateId);
        updateDTO.setUserName("newname");

        when(userRepository.findByUserId(userToUpdateId)).thenReturn(existingUser);
        when(userRepository.findByUserId(actingUserId)).thenReturn(actingUser);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // act
        User result = userService.updateUserByUUID(updateDTO, actingUserId);

        // assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(logTransactionService, times(1)).logAction(any(), any(), any(), any());
    }
    
    @Test
    void testUpdateUserByUUID_UserNotFound() {
        // arrange
        UUID nonexistentId = UUID.randomUUID();
        
        UpdateUserDTO updateDTO = new UpdateUserDTO();
        updateDTO.setUserId(nonexistentId);

        when(userRepository.findByUserId(nonexistentId)).thenReturn(null);

        // act & assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserByUUID(updateDTO, UUID.randomUUID());
        });
        
        assertEquals("User not found", exception.getMessage());
    }
    
    // get all user roles tests
    
    @Test
    void testGetAllRoles() {
        // act
        List<String> roles = userService.getAllRoles();

        // assert
        assertNotNull(roles);
        assertTrue(roles.size() > 0);
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("DEVELOPER"));
        assertTrue(roles.contains("TESTER"));
    }
}
