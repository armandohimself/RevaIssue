package com.abra.revaissue.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.entity.user.UserEnum;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findAllByRole(UserEnum.Role role);
    List<User> findAllUsers();
    User findByUUID(UUID uuid);
    User findByUserName(String userName);
    Boolean existsByUUID(UUID uuid);
}
