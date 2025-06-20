package com.example.User_Service.api.controller;

import com.example.User_Service.api.dto.response.UserInfoDto;
import com.example.User_Service.application.service.UserService;
import com.example.User_Service.common.response.ApiResponse;
import com.example.User_Service.common.util.GatewayRequestHeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 정보 관련 API")
public class UserController {
    private final UserService userService;

    @Operation(summary = "사용자 정보 조회", description = "사용자의 ID를 기반으로 사용자 정보를 조회합니다.")
    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo(id)));
    }

    @Operation(summary = "사용자 정보 수정", description = "현재 로그인된 사용자의 정보를 수정합니다.")
    @PutMapping("/user")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UserInfoDto request){
        Long userId = GatewayRequestHeaderUtils.getUserId();
        return ResponseEntity.ok(ApiResponse.success("회원정보 수정 완료!",userService.updateUserInfo(request,1L ))); // gateway id 넘겨야함
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 계정을 탈퇴 처리합니다.")
    @DeleteMapping("/user")
    public ResponseEntity<ApiResponse> deleteUser(){
        Long userId = GatewayRequestHeaderUtils.getUserId();
        return ResponseEntity.ok(ApiResponse.success("회원탈퇴 성공!",userService.deleteUser(1L)));
    }
}
