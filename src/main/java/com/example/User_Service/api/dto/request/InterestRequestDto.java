package com.example.User_Service.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InterestRequestDto {
    private Long userId;
    private List<String> name;
    
}
