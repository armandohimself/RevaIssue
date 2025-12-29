package com.abra.revaissue.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.UserEnum.Role;
import com.abra.revaissue.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DataLoader {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void preloadAdmin() {
        User admin = userRepository.findByUserName("admin");
        if (admin == null) {
            admin = new User();
            admin.setUserName("admin");
            admin.setPasswordHash(passwordEncoder.encode("password"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
