package com.sparta.lucky.user.presentation;

import com.sparta.lucky.user.application.UserService;
import com.sparta.lucky.user.application.dto.request.UserUpdateCommand;
import com.sparta.lucky.user.common.exception.BusinessException;
import com.sparta.lucky.user.common.exception.UserErrorCode;
import com.sparta.lucky.user.common.response.ApiResponse;
import com.sparta.lucky.user.domain.UserRole;
import com.sparta.lucky.user.presentation.dto.request.UserStatusUpdateRequest;
import com.sparta.lucky.user.presentation.dto.request.UserUpdateReqDto;
import com.sparta.lucky.user.presentation.dto.response.UserResDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/myPage")
    public ResponseEntity<ApiResponse<UserResDto>> getMyProfile(@RequestHeader(value = "X-User-Id") UUID userId) {
        return ResponseEntity.ok(
                ApiResponse.success(UserResDto.from(userService.getUserProfile(userId)))
        );
    }

    @Operation(summary = "특정 사용자 상세 조회")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResDto>> getUserDetail(
            @PathVariable UUID userId,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!UserRole.MASTER.name().equals(role)) {
            throw new BusinessException(UserErrorCode.FORBIDDEN_ACCESS);
        }
        return ResponseEntity.ok(
                ApiResponse.success(UserResDto.from(userService.getUserProfile(userId)))
        );
    }

    @Operation(summary = "전체 사용자 목록 조회 (페이징)")
    @GetMapping
    public ResponseEntity<Page<UserResDto>> getAllUsers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!UserRole.MASTER.name().equals(role)) {
            throw new BusinessException(UserErrorCode.FORBIDDEN_ACCESS);
        }

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "가입 대기 사용자 목록 조회 (PENDING)")
    @GetMapping("/pending")
    public ResponseEntity<Page<UserResDto>> getPendingUsers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader("X-User-Role") String role
    ) {
        if (!UserRole.MASTER.name().equals(role)) {
            throw new BusinessException(UserErrorCode.FORBIDDEN_ACCESS);
        }
        Page<UserResDto> response = userService.getPendingUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "유저 정보 수정 (MASTER 전용)")
    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResDto>> updateUserProfile(
            @PathVariable UUID userId, // 수정 대상 유저
            @RequestHeader(value = "X-User-Id") UUID operatorId, // 수정을 시도하는 사람
            @RequestHeader(value = "X-User-Role") String role,   // 수정을 시도하는 사람의 권한
            @RequestHeader(value = "X-Company-Id", required = false) String companyId,
            @RequestHeader(value = "X-Hub-Id", required = false) String hubId,
            @RequestBody UserUpdateReqDto reqDto){

        if (!UserRole.MASTER.name().equals(role)) {
            throw new BusinessException(UserErrorCode.FORBIDDEN_ACCESS);
        }
        UserUpdateCommand command = UserUpdateCommand.of(userId, role, companyId, hubId, reqDto);
        return ResponseEntity.ok(
                ApiResponse.success(UserResDto.from(userService.updateUser(command)))
        );
    }
    @Operation(summary = "유저 가입 상태 변경")
    @PatchMapping("/{userId}/status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody UserStatusUpdateRequest request,
            @RequestHeader("X-User-Role") String role
    ) {
        // 권한 체크 ( MASTER, HUB_MANAGER만 가능 )
        if (!UserRole.MASTER.name().equals(role) && !UserRole.HUB_MANAGER.name().equals(role)) {
            throw new BusinessException(UserErrorCode.FORBIDDEN_ACCESS);
        }
        userService.updateStatus(userId, request.getStatus());
        return ResponseEntity.ok("성공");
    }

    @Operation(summary = "유저 삭제 (Soft Delete)")
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(
            @PathVariable UUID userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID loginUserId
    ) {
        if (!UserRole.MASTER.name().equals(role)) {
            throw new BusinessException(UserErrorCode.FORBIDDEN_ACCESS);
        }

        userService.deleteUser(userId, loginUserId);

        return ResponseEntity.ok("사용자가 성공적으로 삭제되었습니다.");
    }
}