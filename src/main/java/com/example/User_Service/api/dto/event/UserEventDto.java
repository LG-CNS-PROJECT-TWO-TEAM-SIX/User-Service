package com.example.User_Service.api.dto.event;

import com.example.User_Service.domain.model.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDto {

    public static final String Topic = "user";
    private String action;
    private String name;
    private String email;
    private LocalDateTime eventTime;

}
