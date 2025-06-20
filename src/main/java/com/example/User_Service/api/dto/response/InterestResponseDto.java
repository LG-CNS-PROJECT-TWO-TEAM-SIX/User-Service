package com.example.User_Service.api.dto.response;

import com.example.User_Service.domain.model.Interest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestResponseDto {
    private Long id;
    private String name;

    public static InterestResponseDto from(Interest interest) {
        return InterestResponseDto.builder()
                .id(interest.getId())
                .name(interest.getName())
                .build();
    }
}
