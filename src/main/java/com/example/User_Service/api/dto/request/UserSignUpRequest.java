package com.example.User_Service.api.dto.request;

import com.example.User_Service.domain.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpRequest {
    private String email;
    private String password;
    private String name;
    private List<String> interests;
    private Role role = Role.USER;
}
