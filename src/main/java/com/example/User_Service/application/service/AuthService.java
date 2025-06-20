package com.example.User_Service.application.service;

import com.example.User_Service.api.dto.request.InterestRequestDto;
import com.example.User_Service.api.dto.request.UserLoginRequest;
import com.example.User_Service.api.dto.request.UserSignUpRequest;
import com.example.User_Service.api.dto.response.CheckEmailResponse;
import com.example.User_Service.api.dto.response.CreateNewAccessTokenResponse;
import com.example.User_Service.api.dto.response.UserLoginResponse;
import com.example.User_Service.api.dto.response.UserSignUpResponse;
import com.example.User_Service.common.exception.CustomException;
import com.example.User_Service.common.exception.ErrorCode;
import com.example.User_Service.common.util.PasswordUtil;
import com.example.User_Service.domain.model.RefreshToken;
import com.example.User_Service.domain.model.User;
import com.example.User_Service.domain.repository.UserRepository;
import com.example.User_Service.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final InterestService interestService;
    private final PasswordUtil passwordUtil;

    @Transactional
    public UserLoginResponse login(UserLoginRequest body){
        String email = body.getEmail();
        String password = body.getPassword();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if(!passwordUtil.matches(password, user.getPassword()))
            throw new CustomException(ErrorCode.INVALID_PASSWORD);

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        refreshTokenService.saveRefreshToken(refreshToken, user.getId());
        return UserLoginResponse.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(user.getId()))
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public UserSignUpResponse signUp(UserSignUpRequest body){
        User user = User.builder()
                .email(body.getEmail())
                .name(body.getName())
                .password(passwordUtil.encode(body.getPassword()))
                .role(body.getRole())
                .build();
        List<String> interests = body.getInterests();
        userRepository.save(user);

        InterestRequestDto dto = new InterestRequestDto();
        dto.setName(interests);
        dto.setUserId(user.getId());
        interestService.addInterest(dto);

        return new UserSignUpResponse(user.getId());
    }

    @Transactional
    public CheckEmailResponse isEmailExist(String email){
        return new CheckEmailResponse(userRepository.existsByEmail(email));
    }

    @Transactional
    public void deleteRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenService.findByRefreshToken(token)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
        refreshTokenService.deleteRefreshToken(refreshToken);
    }

    public CreateNewAccessTokenResponse createNewAccessToken(String refreshToken){
        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Long userId = Long.valueOf(jwtTokenProvider.getUserIdByToken(refreshToken));

        return new CreateNewAccessTokenResponse(jwtTokenProvider.generateAccessToken(userId));
    }
}
