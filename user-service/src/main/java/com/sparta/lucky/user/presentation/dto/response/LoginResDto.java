package com.sparta.lucky.user.presentation.dto.response;

import com.sparta.lucky.user.application.dto.response.LoginResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResDto {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;

    public static LoginResDto from(LoginResult result) {
        return LoginResDto.builder()
                .accessToken(result.getAccessToken())
                .refreshToken(result.getRefreshToken())
                .expiresIn(result.getExpiresIn())
                .build();
    }
}