package com.abra.revaissue.util;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.abra.revaissue.enums.UserEnum.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utility class for generating and validating JWT access tokens.
 * Provides methods to create tokens, extract claims, and verify expiration.
 */
@Component
public class JwtUtility {

    private final SecretKey secretKey;

    /**
     * Initializes JwtUtility with the secret key from application properties.
     *
     * @param secret the base64-encoded secret key
     */
    public JwtUtility(@Value("${security.jwt.token.secret-key}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT access token for a given user.
     *
     * @param userId   the UUID of the user
     * @param userName the username
     * @param role     the user's role
     * @return a signed JWT as a String
     */
    public String generateAccessToken(UUID userId, String userName, Role role) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("userName", userName)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Parses and returns all claims from a JWT token.
     *
     * @param token the JWT token
     * @return the claims contained in the token
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the user ID (subject) from the JWT.
     *
     * @param token the JWT token
     * @return the user ID as a String
     */
    public String extractId(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extracts the username from the JWT.
     *
     * @param token the JWT token
     * @return the username
     */
    public String extractUserName(String token) {
        return getClaims(token).get("userName", String.class);
    }

    /**
     * Extracts the role of the user from the JWT.
     *
     * @param token the JWT token
     * @return the user's role
     */
    public Role extractUserRole(String token) {
        return Role.valueOf(getClaims(token).get("role", String.class));
    }

    /**
     * Extracts the expiration date of the JWT.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    /**
     * Checks whether the token has expired.
     *
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates the token against a specific user ID and ensures it is not expired.
     *
     * @param token  the JWT token
     * @param userId the UUID of the user to validate against
     * @return true if the token is valid and belongs to the user, false otherwise
     */
    public boolean validateToken(String token, UUID userId) {
        String tokenUserId = extractId(token);
        return tokenUserId.equals(userId.toString()) && !isTokenExpired(token);
    }

}
