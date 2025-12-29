package com.abra.revaissue.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private UUID userId;
    private String userName;
    private String passwordHash;
    private String role;
}
