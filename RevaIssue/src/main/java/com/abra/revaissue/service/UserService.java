package com.abra.revaissue.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.abra.revaissue.dto.LoginRequestDTO;
import com.abra.revaissue.dto.TokenTransport;
import com.abra.revaissue.dto.UpdateUserDTO;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.EntityType;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LogTransactionService logTransactionService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
    *   Method accepts a user and actingUser entity and saves it to the database
    *   if an admin is creating the account we need to know who it is.     
    * 
    * 
    *   @throws RuntimeException if the username is already taken
    * 
    *   @param user the entity to be created
    *   @param actingUser the entity of whos creating the new user
    *   @return the created user entity
    */
    public User createUser(User user, UUID actingUserId) {
        if (user == null) { return null; }
        
        User actingUser = userRepository.findByUserId(actingUserId);

        User existingUser = userRepository.findByUserName(user.getUserName());
        if (existingUser != null) {
            throw new RuntimeException("Username already taken");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        User createdUser = userRepository.save(user);

        if (actingUser == null) {
            String logMessage = "User registered" + createdUser.getUserName();
            logTransactionService.logAction(logMessage, actingUser, EntityType.USER, createdUser.getUserId());
        } else {
            String logMessage = "User created" + createdUser.getUserName() + " by " + actingUser.getUserName();
            logTransactionService.logAction(logMessage, actingUser, EntityType.USER, createdUser.getUserId());
        }

        return createdUser;
    }

    public TokenTransport login(LoginRequestDTO request) {
        
        User user = userRepository.findByUserName(request.getUserName());
        if (user == null) {
            return null;
        }

        if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            String token = jwtService.createToken(user.getUserId(), request.getUserName());
            return new TokenTransport(token);
        }
        return new TokenTransport();
    }

    /**
    *   Method accepts a user UUID, uses it to find the User and deletes 
    *   them from the database if found.
    *   
    *   @param uuid the UUID of the user to be deleted
    *   @param actingUserId the UUID of the acting user
    *   @return true if the user was successfully deleted, false otherwise
    */
    public boolean deleteUserByUUID(UUID uuid, UUID actingUserId) {
        User existingUser = userRepository.findByUserId(uuid);
        User actingUser = userRepository.findByUserId(actingUserId);
        
        if (existingUser == null) { return false; }
        
        userRepository.delete(existingUser);

        String logMessage = "User deleted: " + existingUser.getUserName() +
                            (actingUser != null ? " by " + actingUser.getUserName() : "");
        logTransactionService.logAction(logMessage, actingUser, EntityType.USER, uuid);

        return !userRepository.existsByUserId(uuid); // T/F
    }

    /**
    *   Method accepts a user UUID and an updated user entity,
    *   updates the existing user in the database with the new information
    *     
    *   @throws RuntimeException if the user with the given UUID is not found
    * 
    *   @param uuid the UUID of the user doing the updating
    *   @param updatedUser the user entity with updated information
    *   @return the updated user entity
    */
    public User updateUserByUUID(UpdateUserDTO updatedUser, UUID actingUserId) {
        User existingUser = userRepository.findByUserId(updatedUser.getUserId());
        User actingUser = userRepository.findByUserId(actingUserId);
        
        if (existingUser == null) { 
            throw new RuntimeException("User not found"); 
        }
        if (updatedUser.getUserName() != null) {
            existingUser.setUserName(updatedUser.getUserName());
        }
        if (updatedUser.getPasswordHash() != null) {
            existingUser.setPasswordHash(updatedUser.getPasswordHash());
        }
        if (updatedUser.getRole() != null) {
            existingUser.setRole(Role.valueOf(updatedUser.getRole()));
        }

        User savedUser = userRepository.save(existingUser);

        String logMessage = "User updated: " + savedUser.getUserName() +
                            (actingUser != null ? " by " + actingUser.getUserName() : "");
        logTransactionService.logAction(logMessage, actingUser, EntityType.USER, savedUser.getUserId());

        return savedUser;
    }

    /**
     * Method accepts a UUID of the user to be found.
     * 
     * @throws RuntimeException if the user does not exist by that UUID
     * 
     * @param uuid the UUID of the user to be returned
     * @return the user entity
    */
    public User getUserByUUID(UUID uuid) {
        if (userRepository.findByUserId(uuid) == null){
            throw new RuntimeException("User not found");
        }
        return userRepository.findByUserId(uuid);
    }

    /**
     *  Method to get all Users from the database
     *  
     *  @return a list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     *  Method to get all Users with a certain role
     * 
     *  @param role takes in the role of users we want to find
     *  @return a list of all users that have a specific role
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    /**
     * Method to get all the roles
     * 
     * @return a list of all roles that are possible in the system
     */
    public List<String> getAllRoles() {
        List<String> roles = Stream.of(Role.values())
                              .map(Role::name)
                              .collect(Collectors.toList());
        return roles;
    }
}
