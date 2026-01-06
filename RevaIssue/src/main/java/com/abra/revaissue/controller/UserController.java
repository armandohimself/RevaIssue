package com.abra.revaissue.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abra.revaissue.dto.LoginRequestDTO;
import com.abra.revaissue.dto.TokenTransport;
import com.abra.revaissue.dto.UpdateUserDTO;
import com.abra.revaissue.dto.project.ProjectMapper;
import com.abra.revaissue.dto.project.ProjectResponse;
import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.service.AuthzService;
import com.abra.revaissue.service.ProjectAccessService;
import com.abra.revaissue.service.UserService;

import java.util.List;
import java.util.UUID;

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

    private final ProjectAccessService projectAccessService;

    private final UserService userService;
    private final AuthzService authzService;

    public UserController(
        AuthzService authzService,
        UserService userService,
        ProjectAccessService projectAccessService
    ) {
        this.projectAccessService = projectAccessService;
        this.authzService = authzService;
        this.userService = userService;
    }

    // post users - create a new user (admin only, for onboarding testers/developers)
    @PostMapping("/create")
    public ResponseEntity<?> createUser(
        @RequestBody User user,
        @RequestHeader(name = "Authorization") String actingUserToken
    ) {
        UUID actingUserId = authzService.actingUserId(actingUserToken);

        authzService.mustBeAdmin(actingUserId);

        User createdUser = userService.createUser(user, actingUserId);
        return ResponseEntity.ok(createdUser);
    }

    // login - accepts username and password, returns jwt if valid
    @PostMapping("/login")
    public ResponseEntity<TokenTransport> login(@RequestBody LoginRequestDTO request) {

        TokenTransport token = userService.login(request);

        /**
         * We should not be failing on 500 on bad credentials
         * token.getToken() != "" is a reference comparison, not a content check => use .isBlank()
         */
        if(token == null || token.getToken() == null || token.getToken().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(token);
    }

    // log out - ?

    // get the current user from jwt token
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(
        @RequestHeader(name = "Authorization") String token
    ) {
        UUID userId = authzService.actingUserId(token);
        User user = userService.getUserByUUID(userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }

    // Made this in case you still wanted to read from the projects but through your own route
    // Better to keep the assign and revoke logic in one place rather than in multiple places
    @GetMapping("/me/projects")
    public ResponseEntity<List<ProjectResponse>> myProjects(
        @RequestHeader(name = "Authorization") String authHeader
    ) {

        UUID userId = authzService.actingUserId(authHeader);

        List<ProjectResponse> projects = projectAccessService.listActiveProjectsForUser(userId)
            .stream()
            .map(ProjectMapper::toResponse)
            .toList();

        return ResponseEntity.ok(projects);
    }

    // get users/{id} - get user details
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserByUUID(userId));
    }

    // get users - list all users (admin only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(
        @RequestHeader(name = "Authorization") String actingUserToken
    ){
        UUID actingUserId = authzService.actingUserId(actingUserToken);

        authzService.mustBeAdmin(actingUserId);

        return ResponseEntity.ok(userService.getAllUsers());
    }

    // put users/{id} - update user details
    @PutMapping("/update/{userToUpdateId}")
    public ResponseEntity<?> updateUser(    // if we want to use "id" as path variable
            @PathVariable UUID userToUpdateId, // -> @PathVariable("id") UUID userToUpdateId
            @RequestBody UpdateUserDTO userToUpdate,
            @RequestHeader(name = "Authorization") String actingUserToken
        ) {

        UUID actingUserId = authzService.actingUserId(actingUserToken);

        authzService.mustBeAdmin(actingUserId);

        userToUpdate.setUserId(userToUpdateId);
        User updatedUser = userService.updateUserByUUID(userToUpdate, actingUserId);
        return ResponseEntity.ok(updatedUser);
    }

    // delete users/{id} - delete user (admin only)
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUserByUUID(
        @PathVariable UUID userId,
        @RequestHeader(name = "Authorization") String actingUserToken
    ) {
        UUID actingUserId = authzService.actingUserId(actingUserToken);

        authzService.mustBeAdmin(actingUserId);

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
}
