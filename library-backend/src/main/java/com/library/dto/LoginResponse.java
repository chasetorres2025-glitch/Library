package com.library.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private Long userId;

    private String username;

    private String realName;

    private String roleCode;

    public LoginResponse(String token, Long userId, String username, String realName, String roleCode) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.roleCode = roleCode;
    }

}
