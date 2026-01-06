package com.abra.revaissue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    /**
     * @JsonProperty("username") is optional now that I rewrote the /login route
     * This just means it also accepts username or userName in the json
     */
    @JsonProperty("userName")
    String userName;
    String password;
}
