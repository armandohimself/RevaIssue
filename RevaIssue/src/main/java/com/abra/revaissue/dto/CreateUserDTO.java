package com.abra.revaissue.dto;

import com.abra.revaissue.enums.UserEnum.Role;

import lombok.Data;

@Data
public class CreateUserDTO { 
    private String userName;
    private String password;
    private Role role;
}
