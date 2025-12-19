package com.abra.revaissue.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abra.revaissue.entity.user.User;
import com.abra.revaissue.entity.user.UserEnum;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByRole(UserEnum.Role role);
    List<User> findAllUsers();
}
