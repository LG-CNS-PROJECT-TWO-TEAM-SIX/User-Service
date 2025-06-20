package com.example.User_Service.application.service;

import com.example.User_Service.api.dto.request.FavoriteRequestDto;
import com.example.User_Service.api.dto.response.FavoriteResponseDto;
import com.example.User_Service.common.exception.CustomException;
import com.example.User_Service.common.exception.ErrorCode;
import com.example.User_Service.domain.model.Favorite;
import com.example.User_Service.domain.model.User;
import com.example.User_Service.domain.repository.FavoriteRepository;
import com.example.User_Service.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    @Transactional
    public Optional<FavoriteResponseDto> likeNews(FavoriteRequestDto dto){
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));


        Optional<Favorite> existing = favoriteRepository.findByUserAndNewsLink(user,dto.getNewsLink());
        
        if(existing.isPresent()){
            favoriteRepository.delete(existing.get());
            return Optional.empty();
        }
            
        Favorite favorite = Favorite.builder()
            .user(user)
            .newsTitle(dto.getNewsTitle())
            .newsSummary(dto.getNewsSummary())
            .newsLink(dto.getNewsLink())
            .newsThumbnail(dto.getNewsThumbnail())
            .newsCategory(dto.getNewsCategory())
            .build();

        Favorite saved = favoriteRepository.save(favorite);
        return Optional.of(FavoriteResponseDto.from(saved));
        
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponseDto> getMyFavorites(Long userId){
        return favoriteRepository.findByUserId(userId).stream()
        .map(FavoriteResponseDto::from)
        .collect(Collectors.toList());
    }

    @Transactional
    public void cancelLike(Long id){
        Favorite favorite = favoriteRepository.findById(id)
            .orElseThrow(()-> new CustomException(ErrorCode.FAVORITE_NOT_FOUND));
        favoriteRepository.delete(favorite);
    }

    
}
