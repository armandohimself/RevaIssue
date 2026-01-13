package com.abra.revaissue.service;

import java.util.UUID;

import com.abra.revaissue.entity.user.User;
// import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.exception.ForbiddenOperationException;
import com.abra.revaissue.exception.UnauthenticatedException;
import com.abra.revaissue.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.stereotype.Service;

@Service
public class AuthzService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthzService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    // I am the source of truth!
    //! Must Be Admin
    public void mustBeAdmin(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("The user was not found!"));

        if(user.getRole() != Role.ADMIN) {
            throw new ForbiddenOperationException("Admin privileges required!");
        }
    }

    // Optional convenience, please use above as DB source of truth
    public void mustBeAdmin(Role role) {
        if (role != Role.ADMIN) throw new ForbiddenOperationException("Admin privileges required!");
    }

    //! Acting User Token Check
    public UUID actingUserId(String authHeader) {
        // Guard rails
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthenticatedException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        try {
            if (jwtService.isTokenExpired(token)) {
                throw new UnauthenticatedException("Token expired!");
            }
            return jwtService.getUserIdFromToken(token);
        } catch (ExpiredJwtException error) {
            throw new UnauthenticatedException("Token expired. Please log in again.");
        }
    }
}
