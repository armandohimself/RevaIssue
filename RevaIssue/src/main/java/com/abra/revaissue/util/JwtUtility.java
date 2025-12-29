package com.abra.revaissue.util;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtility {

    // private final SecretKey secretKey;

    // // requires environment variable JWT.SECRET to be set for use
    // // currently just use the one below for testing
    // public JwtUtility(@Value("${JWT.SECRET}") String secret) {
    // byte[] keyBytes = Base64.getDecoder().decode(secret);
    // this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    // }

    private final String SECRET_KEY = "your-key-should-be-at-least-32-bytes";

    public String generateAccessToken(UUID userId, String userName) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("userName", userName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), Jwts.SIG.HS256) // if using constructor replace
                                                                                     // args
                                                                                     // with secretKey
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())) // if using constructor replace args with
                                                                       // secretKey
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractId(String token) {
        return getClaims(token).getSubject();
    }

    public String extractUserName(String token) {
        return getClaims(token).get("username", String.class);
    }

    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UUID userId) {
        String tokenUserId = extractId(token);
        String tokenUsername = extractUserName(token);
        return tokenUserId.equals(userId.toString()) && tokenUsername.equals(tokenUsername) && !isTokenExpired(token);
    }

}
