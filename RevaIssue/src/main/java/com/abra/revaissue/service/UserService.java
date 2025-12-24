package com.abra.revaissue.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    /**
    *    this is where we would send back a DTO instead of the 
    *    entity as to not expose sensitive information
    */

    /**
    *   Method accepts a user entity and saves it to the database
    *   
    *   @throws RuntimeException if the username is already taken
    * 
    *   @param user the entity to be created
    *   @return the created user entity
    */
    public User createUser(User user) {
        if (user == null) { return null; }
    
        User existingUser = userRepository.findByUserName(user.getUserName());
        if (existingUser != null) {
            throw new RuntimeException("Username already taken");
        }

        return userRepository.save(user);
    }

    /**
    *   Method accepts a user UUID, uses it to find the User and deletes 
    *   them from the database if found.
    *   
    *   @param uuid the UUID of the user to be deleted
    *   @return true if the user was successfully deleted, false otherwise
    */
    public boolean deleteUserByUUID(UUID uuid) {
        User existingUser = userRepository.findByUserId(uuid);
        
        if (existingUser == null) { return false; }
        
        userRepository.delete(existingUser);

        return userRepository.existsByUserId(uuid); // T/F
    }

    /**
    *   Method accepts a user UUID and an updated user entity,
    *   updates the existing user in the database with the new information
    *     
    *   @throws RuntimeException if the user with the given UUID is not found
    * 
    *   @param uuid the UUID of the user to be updated
    *   @param updatedUser the user entity with updated information
    *   @return the updated user entity
    */
    public User updateUserByUUID(UUID uuid, User updatedUser) {
        User existingUser = userRepository.findByUserId(uuid);
        
        if (existingUser == null) { throw new RuntimeException("User not found"); }

        existingUser.setUserName(updatedUser.getUserName());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setRole(updatedUser.getRole());

        return userRepository.save(existingUser);
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
}
