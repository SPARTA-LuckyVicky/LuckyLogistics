package com.sparta.lucky.user.presentation;

import com.sparta.lucky.user.application.AuthService;
import com.sparta.lucky.user.application.dto.request.LoginCommand;
import com.sparta.lucky.user.application.dto.request.SignupCommand;
import com.sparta.lucky.user.common.response.ApiResponse;
import com.sparta.lucky.user.presentation.dto.request.LoginReqDto;
import com.sparta.lucky.user.presentation.dto.request.LogoutReqDto;
import com.sparta.lucky.user.presentation.dto.request.SignupReqDto;
import com.sparta.lucky.user.presentation.dto.request.TokenRefreshReqDto;
import com.sparta.lucky.user.presentation.dto.response.LoginResDto;
import com.sparta.lucky.user.presentation.dto.response.SignupResDto;
import com.sparta.lucky.user.presentation.dto.response.TokenResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Auth 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResDto>> signup(@Valid @RequestBody SignupReqDto reqDto) {

        SignupCommand command = SignupCommand.from(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SignupResDto.from(authService.signUp(command))));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResDto>> login(@Valid @RequestBody LoginReqDto reqDto) {
        LoginCommand command = new LoginCommand(reqDto.getUsername(), reqDto.getPassword());

        return ResponseEntity.ok(
                ApiResponse.success(LoginResDto.from(authService.login(command)))
        );
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResDto>> refresh(@Valid @RequestBody TokenRefreshReqDto request) {
        TokenResDto response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody LogoutReqDto request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다."));
    }
}
