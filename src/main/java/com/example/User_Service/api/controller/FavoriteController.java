package com.example.User_Service.api.controller;


import com.example.User_Service.api.dto.request.FavoriteRequestDto;
import com.example.User_Service.api.dto.response.FavoriteResponseDto;
import com.example.User_Service.application.service.FavoriteService;
import com.example.User_Service.common.response.ApiResponse;
import com.example.User_Service.common.util.GatewayRequestHeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/v1")
@Tag(name = "Favorite API", description = "뉴스 좋아요 관련 API")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @Operation(summary = "뉴스 좋아요 등록", description = "사용자가 특정 뉴스에 좋아요를 등록합니다.")
    @PostMapping("/favorite")
    public ResponseEntity<?> likeNews(@RequestBody FavoriteRequestDto dto) {
        return favoriteService.likeNews(dto)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElseGet(()-> ResponseEntity.noContent().build());
    }

    @Operation(summary = "좋아요한 뉴스 조회", description = "특정 사용자가 좋아요를 누른 뉴스 목록을 조회합니다.")
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse> getDefaultFavorites() {
        Long userId = GatewayRequestHeaderUtils.getUserId();
        List<FavoriteResponseDto> list = favoriteService.getMyFavorites(userId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
    @Operation(summary = "좋아요한 뉴스 조회", description = "특정 사용자가 좋아요를 누른 뉴스 목록을 조회합니다.")
    @GetMapping("/favorites/{userId}")
    public ResponseEntity<ApiResponse> getFavorites(@PathVariable Long userId) {
        List<FavoriteResponseDto> list = favoriteService.getMyFavorites(userId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @Operation(summary = "뉴스 좋아요 취소", description = "좋아요를 누른 뉴스에 대해 좋아요를 취소합니다.")
    @DeleteMapping("/favorite/{id}")
    public ResponseEntity<Void> cancelLike(@PathVariable Long id){
        favoriteService.cancelLike(id);
        return ResponseEntity.noContent().build();
    }
}
