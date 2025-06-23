package com.example.User_Service.api.controller;

import com.example.User_Service.api.dto.request.InterestRequestDto;
import com.example.User_Service.api.dto.response.InterestResponseDto;
import com.example.User_Service.application.service.InterestService;
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
@Tag(name = "Interest API", description = "사용자 관심사 관련 API")
public class InterestController {
    private final InterestService interestService;

    @Operation(summary = "사용자 관심사 등록", description = "사용자의 관심사를 등록합니다.")
    @PostMapping("/interest")
    public ResponseEntity<Void> addInterest(@RequestBody InterestRequestDto dto) {
        interestService.addInterest(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 관심사 조회", description = "특정 사용자의 관심사 목록을 조회합니다.")
    @GetMapping("/interest")
    public ResponseEntity<ApiResponse> getDefaultInterests() {
        Long userId = GatewayRequestHeaderUtils.getUserId();
        List<InterestResponseDto> list = interestService.getInterests(userId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @Operation(summary = "사용자 관심사 조회", description = "특정 사용자의 관심사 목록을 조회합니다.")
    @GetMapping("/interest/{userId}")
    public ResponseEntity<ApiResponse> getInterests(@PathVariable Long userId) {
        List<InterestResponseDto> list = interestService.getInterests(userId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @Operation(summary = "사용자 관심사 삭제", description = "특정 사용자의 관심사를 삭제합니다.")
    @DeleteMapping("/interest/delete")
    public ResponseEntity<Void> removeInterest(@RequestBody InterestRequestDto dto) {
        Long userId = GatewayRequestHeaderUtils.getUserId();
        dto.setUserId(userId);
        interestService.removeInterest(dto);
        return ResponseEntity.noContent().build();
    }
}
