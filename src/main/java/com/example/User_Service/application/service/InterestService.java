package com.example.User_Service.application.service;

import com.example.User_Service.api.dto.request.InterestRequestDto;
import com.example.User_Service.api.dto.response.InterestResponseDto;
import com.example.User_Service.common.exception.CustomException;
import com.example.User_Service.common.exception.ErrorCode;
import com.example.User_Service.domain.model.Interest;
import com.example.User_Service.domain.model.User;
import com.example.User_Service.domain.model.UserInterest;
import com.example.User_Service.domain.repository.InterestRepository;
import com.example.User_Service.domain.repository.UserInterestRepository;
import com.example.User_Service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterestService {
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final UserInterestRepository userInterestRepository;

    @Transactional
    public void addInterest(InterestRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        for (String name:dto.getName()){
            Interest interest = interestRepository.findByName(name)
                .orElseGet(()->interestRepository.save(
                    Interest.builder().name(name).build()
                ));
            if(userInterestRepository.existsByUserAndInterest(user, interest)){
                throw new CustomException(ErrorCode.DUPLICATE_INTEREST);
            }

            UserInterest mapping = UserInterest.builder()
                .user(user)
                .interest(interest)
                .build();
            userInterestRepository.save(mapping);
        }
    }

    @Transactional
    public void removeInterest(InterestRequestDto dto){
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        for(String name:dto.getName()){
            Interest interest = interestRepository.findByName(name)
                .orElseThrow(()->new CustomException(ErrorCode.INTEREST_NOT_FOUND));
            if(!userInterestRepository.existsByUserAndInterest(user, interest)){
                throw new CustomException(ErrorCode.INTEREST_NOT_FOUND_FOR_USER);
            }
            userInterestRepository.deleteByUserAndInterest(user, interest);
        }
    }
    

    @Transactional(readOnly = true)
    public List<InterestResponseDto> getInterests(Long userId){
        User user = userRepository.findById(userId)
            .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        
        return userInterestRepository.findByUserId(userId).stream()
            .map(UserInterest::getInterest)
            .map(InterestResponseDto::from)
            .collect(Collectors.toList());
    }
    
}
