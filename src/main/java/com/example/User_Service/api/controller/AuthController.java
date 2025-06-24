package com.example.User_Service.api.controller;

import com.example.User_Service.api.dto.request.CheckEmailRequest;
import com.example.User_Service.api.dto.request.UserLoginRequest;
import com.example.User_Service.api.dto.request.UserSignUpRequest;
import com.example.User_Service.api.dto.response.CheckEmailResponse;
import com.example.User_Service.api.dto.response.CreateNewAccessTokenResponse;
import com.example.User_Service.api.dto.response.UserLoginResponse;
import com.example.User_Service.api.dto.response.UserSignUpResponse;
import com.example.User_Service.application.service.AuthService;
import com.example.User_Service.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "사용자 인증/인가 관련 API")
@RequestMapping("/api/user/v1/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;
    @GetMapping("/test")
    public String test(){
        return "good";
    }

    @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호를 입력받아 JWT 액세스/리프레시 토큰을 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody UserLoginRequest request){
        return ResponseEntity.ok(ApiResponse.success(authService.login(request))); //메세지 없이 데이터만 응답
    }

    @Operation(summary = "회원가입", description = "이름, 이메일, 비밀번호를 입력받아 회원가입을 진행합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse> signUp(@RequestBody UserSignUpRequest request){
        log.info("Sign-up Request : {}. {}, {}. {}",request.getEmail(),request.getName(),request.getPassword(),request.getRole());
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공!",authService.signUp(request))); //메세지와 데이터 함께 응답
    }

    @Operation(summary = "이메일 중복 확인", description = "이메일이 이미 존재하는지 확인합니다.")
    @PostMapping("/email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestBody CheckEmailRequest request){
        return ResponseEntity.ok(ApiResponse.success(authService.isEmailExist(request.getEmail())));
    }

    @Operation(summary = "새로운 액세스 토큰 발급", description = "리프레시 토큰을 통해 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/token")
    public ResponseEntity<ApiResponse> createNewAccessToken(@RequestHeader(value = "refreshToken") String refreshToken){
        return ResponseEntity.ok(ApiResponse.success(authService.createNewAccessToken(refreshToken)));
    }

    @Operation(summary = "로그아웃", description = "리프레시 토큰을 삭제하여 로그아웃을 처리합니다.")
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "refreshToken") String refreshToken){
        authService.deleteRefreshToken(refreshToken);
        return ResponseEntity.ok("Logout Successful");
    }
}
