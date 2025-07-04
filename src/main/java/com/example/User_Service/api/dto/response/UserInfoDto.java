package com.example.User_Service.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserInfoDto {
    private String email;
    private String name;
    private List<String> interests;
}
