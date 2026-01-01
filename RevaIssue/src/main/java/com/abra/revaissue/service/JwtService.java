package com.abra.revaissue.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.util.JwtUtility;

@Service
public class JwtService {

    private final JwtUtility jwtUtility;

    @Autowired
    public JwtService(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    public String createToken(UUID userId, String userName, Role role) {
        return jwtUtility.generateAccessToken(userId, userName, role);
    }

    public boolean validateToken(String token, UUID userId) {
        return jwtUtility.validateToken(token, userId);
    }

    public String getUserNameFromToken(String token) {
        return jwtUtility.extractUserName(token);
    }

    public UUID getUserIdFromToken(String token) {
        String userIdStr = jwtUtility.extractId(token);
        return UUID.fromString(userIdStr);
    }

    public boolean isTokenExpired(String token) {
        return jwtUtility.isTokenExpired(token);
    }

    public Date getTokenExpirationDate(String token) {
        return jwtUtility.extractExpiration(token);
    }

}
