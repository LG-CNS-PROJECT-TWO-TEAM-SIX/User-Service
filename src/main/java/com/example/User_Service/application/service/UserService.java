package com.example.User_Service.application.service;

import com.example.User_Service.api.dto.event.UserEventDto;
import com.example.User_Service.api.dto.request.InterestRequestDto;
import com.example.User_Service.api.dto.response.UserInfoDto;
import com.example.User_Service.common.exception.CustomException;
import com.example.User_Service.common.exception.ErrorCode;
import com.example.User_Service.domain.model.User;
import com.example.User_Service.domain.repository.FavoriteRepository;
import com.example.User_Service.domain.repository.UserInterestRepository;
import com.example.User_Service.domain.repository.UserRepository;
import com.example.User_Service.infrastructure.kafka.UserEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InterestService interestService;
    private final UserInterestRepository userInterestRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserEventProducer producer;

    @Transactional
    public UserInfoDto getUserInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserInfoDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserInfoDto updateUserInfo(UserInfoDto request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.update(request.getEmail(), request.getName());
        userRepository.save(user);
        userInterestRepository.deleteByUser(user);

        List<String> interests = request.getInterests();
        InterestRequestDto dto = new InterestRequestDto();
        dto.setUserId(user.getId());
        dto.setName(interests);
        interestService.addInterest(dto);

        return UserInfoDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .interests(interests)
                .build();
    }

    @Transactional
    public String deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        favoriteRepository.deleteByUser(user);
        userInterestRepository.deleteByUser(user);
        userRepository.delete(user);

        UserEventDto message = UserEventDto.builder()
                .eventTime(LocalDateTime.now())
                .name(user.getName())
                .action("WITHDRAWAL")
                .build();

        producer.send(UserEventDto.Topic, message);
        return "User Deleted";
    }
}
