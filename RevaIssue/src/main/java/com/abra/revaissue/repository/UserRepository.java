package com.abra.revaissue.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.enums.UserEnum;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findAllByRole(UserEnum.Role role);
    User findByUserId(UUID uuid);
    User findByUserName(String userName);
    Boolean existsByUserId(UUID uuid);
}
