package com.abra.revaissue.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private UUID userId;
    private String userName;
    private String passwordHash;
    private String role;
}
