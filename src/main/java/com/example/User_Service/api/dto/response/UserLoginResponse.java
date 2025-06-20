package com.example.User_Service.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponse {
    private String accessToken;
    private String refreshToken;
}
