package com.example.User_Service.api.dto.event;

import com.example.User_Service.domain.model.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserEventDto {

    public static final String Topic = "user";
    private String action;
    private String name;
    private LocalDateTime eventTime;

}
