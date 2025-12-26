package com.abra.revaissue.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abra.revaissue.util.JwtUtility;

@Service
public class JwtService {

    private final JwtUtility jwtUtility;

    @Autowired
    public JwtService(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

}
