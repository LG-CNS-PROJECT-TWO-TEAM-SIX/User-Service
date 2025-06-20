package com.example.User_Service.api.dto.request;

import lombok.Getter;

@Getter
public class UserLoginRequest {
    private String email;
    private String password;
}
