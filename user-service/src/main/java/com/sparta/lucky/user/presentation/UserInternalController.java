package com.sparta.lucky.user.presentation;

import com.sparta.lucky.user.application.UserService;
import com.sparta.lucky.user.application.dto.response.UserResult;
import com.sparta.lucky.user.common.response.ApiResponse;
import com.sparta.lucky.user.presentation.dto.response.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/api/v1/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userService;

    /**
     * 타 서비스(Order 등)에서 FeignClient로 유저 정보를 조회할 때 사용
     */
    @GetMapping("/{userId}")
    public ApiResponse<UserResDto> getUserInternal(
            @PathVariable UUID userId,
            @RequestHeader(value = "X-Internal-Request", required = false) String internalRequest
    ) {
        // 내부 통신 여부를 간단히 체크할 수 있습니다.
        UserResult userResult = userService.getUserProfile(userId);
        return ApiResponse.success(UserResDto.from(userResult));
    }
}