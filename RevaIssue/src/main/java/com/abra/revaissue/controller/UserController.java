package com.abra.revaissue.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abra.revaissue.dto.LoginRequestDTO;
import com.abra.revaissue.dto.TokenTransport;
import com.abra.revaissue.dto.UpdateUserDTO;
import com.abra.revaissue.entity.ProjectAccess;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.ProjectRole;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.service.JwtService;
import com.abra.revaissue.service.ProjectAccessService;
import com.abra.revaissue.service.UserService;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ProjectAccessService projectAccessService;

    // post users - create a new user (admin only, for onboarding testers/developers)
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user, @RequestHeader(name = "Authorization") String actingUserToken) {
        String slicedToken = actingUserToken.split(" ")[1];
        UUID actingUserId = jwtService.getUserIdFromToken(slicedToken);

        if (!isAdmin(actingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The user is not an ADMIN.");
        }

        User createdUser = userService.createUser(user, actingUserId);
        return ResponseEntity.ok(createdUser);
    }

    // login - accepts username and passwrod, returns jwt if valid
    @PostMapping("/login")
    public ResponseEntity<TokenTransport> login(@RequestBody LoginRequestDTO request) {
        
        TokenTransport token = userService.login(request);
        if (token.getToken() != "") {
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    // log out - ?

    // get the current user from jwt token
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader(name = "Authorization") String token) {
        String slicedToken = token.split(" ")[1];
        UUID userId = jwtService.getUserIdFromToken(slicedToken);
        User user = userService.getUserByUUID(userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }

    // get users/{id} - get user details
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserByUUID(userId));
    }

    // get users - list all users (admin only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader(name = "Authorization") String actingUserToken){
        String slicedToken = actingUserToken.split(" ")[1];
        UUID actingUserId = jwtService.getUserIdFromToken(slicedToken);
        
        if (!isAdmin(actingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The user is not an ADMIN.");
        } else {
            return ResponseEntity.ok(userService.getAllUsers());
        }
    }

    // put users/{id} - update user details
    @PutMapping("/update/{userToUpdateId}")
    public ResponseEntity<?> updateUser(    // if we want to use "id" as path variable
            @PathVariable UUID userToUpdateId, // -> @PathVariable("id") UUID userToUpdateId
            @RequestBody UpdateUserDTO userToUpdate, 
            @RequestHeader(name = "Authorization") String actingUserToken) {
        
        String slicedToken = actingUserToken.split(" ")[1];
        UUID actingUserId = jwtService.getUserIdFromToken(slicedToken);

        if (!isAdmin(actingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The user is not an ADMIN.");
        }
        userToUpdate.setUserId(userToUpdateId);
        User updatedUser = userService.updateUserByUUID(userToUpdate, actingUserId);
        return ResponseEntity.ok(updatedUser);
    }

    // delete users/{id} - delete user (admin only) 
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserByUUID(@PathVariable UUID userId, @RequestHeader(name = "Authorization") String actingUserToken) {
        String slicedToken = actingUserToken.split(" ")[1];
        UUID actingUserId = jwtService.getUserIdFromToken(slicedToken);

        if (!isAdmin(actingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("The user is not an ADMIN.");
        }

        Boolean wasUserDeleted = userService.deleteUserByUUID(userId, actingUserId);

        if (wasUserDeleted == true) {
            return ResponseEntity.ok().body("User was deleted");
        } else {
            return ResponseEntity.badRequest().body("User was not deleted");
        }
    }

    // get /users/roles - list available roles
    @GetMapping("/roles")
    public List<String> getUserRoles() {
        return userService.getAllRoles();
    }

    // post /users/{userId}/projects/{projectId} - adding users to a project
    @PostMapping("/{usersId}/projects/{projectId}")
    public ResponseEntity<?> addUserToProject(
            @PathVariable UUID userId,
            @PathVariable UUID projectId,
            @RequestHeader(name = "Authorization") String actingUserToken,
            @RequestBody String projectRole) {
        
        String slicedToken = actingUserToken.split(" ")[1];
        UUID actingUserId = jwtService.getUserIdFromToken(slicedToken);

        if (!isAdmin(actingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN can add users to projects.");
        }

        ProjectRole role = ProjectRole.valueOf(projectRole);
        ProjectAccess projectAccess = new ProjectAccess();
        projectAccess.setProjectId(projectId);
        projectAccess.setUserId(userId);
        projectAccess.setProjectRole(role);
        projectAccess.setAssignedByUserId(actingUserId);
        ProjectAccess access = projectAccessService.assignAccess(projectAccess, actingUserId);
        return ResponseEntity.ok(access);
    }
    

    // delete /users/{userId}/projects/{projectId} - removing users from a project
    @DeleteMapping("/{userId}/projects/{projectId}")
    public ResponseEntity<?> removeUserFromProject(
        @PathVariable UUID userId,
        @PathVariable UUID projectId,
        @RequestHeader(name = "Authorization") String actingUserToken) {
        
        String slicedToken = actingUserToken.split(" ")[1];
        UUID actingUserId = jwtService.getUserIdFromToken(slicedToken);

        if (!isAdmin(actingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN can remove users from projects.");
        }

        ProjectAccess revoked = projectAccessService.revokeAccess(projectId, userId, actingUserId);
        return ResponseEntity.ok(revoked);
    }

    // helper method
    private boolean isAdmin(UUID userId) {
        User user = userService.getUserByUUID(userId);
        return user != null && user.getRole() == Role.ADMIN;
    }
}
