package com.abra.revaissue.service;

import java.util.UUID;

import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class AuthzService {
    private final UserRepository userRepository;

    public AuthzService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void mustBeAdmin(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        if(user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Admin privilege required to perform action!");
        }
    }
}
