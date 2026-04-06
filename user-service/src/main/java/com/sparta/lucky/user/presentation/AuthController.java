package com.sparta.lucky.user.presentation;

import com.sparta.lucky.user.application.AuthService;
import com.sparta.lucky.user.application.dto.response.SignupResult;
import com.sparta.lucky.user.common.response.ApiResponse;
import com.sparta.lucky.user.presentation.dto.request.SignupReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ApiResponse<SignupResult> signup(@Valid @RequestBody SignupReqDto reqDto){
        SignupResult signupResult = authService.signup(reqDto);
        return ApiResponse.success(signupResult);
    public ResponseEntity<ApiResponse<SignupResDto>> signup(@Valid @RequestBody SignupReqDto reqDto) {

        SignupCommand command = SignupCommand.from(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SignupResDto.from(authService.signUp(command))));
    }

    }
}
