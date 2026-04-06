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
    public ResponseEntity<ApiResponse<SignupResDto>> signup(@Valid @RequestBody SignupReqDto reqDto) {

        SignupCommand command = SignupCommand.from(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SignupResDto.from(authService.signUp(command))));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResDto>> login(@RequestBody LoginReqDto reqDto) {
        LoginCommand command = new LoginCommand(reqDto.getUsername(), reqDto.getPassword());

        return ResponseEntity.ok(
                ApiResponse.success(LoginResDto.from(authService.login(command)))
        );
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResDto> refresh(@RequestBody TokenRefreshReqDto request) {
        TokenResDto response = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutReqDto request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}
