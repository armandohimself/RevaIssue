package com.abra.revaissue.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.util.JwtUtility;

/**
 * Service layer for handling JWT-related operations.
 * Acts as an abstraction over JwtUtility for use in controllers.
 */
@Service
public class JwtService {

    private final JwtUtility jwtUtility;

    /**
     * Constructs JwtService with required JwtUtility dependency.
     * Spring sees only one constructor, so it auto-injects @Autowired for you, it was optional at that point.
     * @param jwtUtility utility for JWT creation and validation
     */
    public JwtService(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    /**
     * Creates a JWT access token for a user.
     *
     * @param userId   the user's UUID
     * @param userName the user's username
     * @param role     the user's role
     * @return a signed JWT token
     */
    public String createToken(UUID userId, String userName, Role role) {
        return jwtUtility.generateAccessToken(userId, userName, role);
    }

    /**
     * Validates a JWT token against a specific user.
     *
     * @param token  the JWT token
     * @param userId the user UUID to validate against
     * @return true if token is valid and not expired
     */
    public boolean validateToken(String token, UUID userId) {
        return jwtUtility.validateToken(token, userId);
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUserNameFromToken(String token) {
        return jwtUtility.extractUserName(token);
    }

    /**
     * Extracts the user's role from a JWT token.
     *
     * @param token the JWT token
     * @return the user's role
     */
    public Role getUserRoleFromToken(String token) {
        return jwtUtility.extractUserRole(token);
    }

    /**
     * Extracts the user's role from a JWT token.
     *
     * @param token the JWT token
     * @return the user's role
     */
    public UUID getUserIdFromToken(String token) {
        String userIdStr = jwtUtility.extractId(token);
        return UUID.fromString(userIdStr);
    }

    /**
     * Checks whether the JWT token has expired.
     *
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return jwtUtility.isTokenExpired(token);
    }

    /**
     * Checks whether the JWT token has expired.
     *
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    public Date getTokenExpirationDate(String token) {
        return jwtUtility.extractExpiration(token);
    }

}
