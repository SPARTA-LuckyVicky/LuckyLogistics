package com.sparta.lucky.user.application.dto.response;

import lombok.*;
import org.keycloak.representations.AccessTokenResponse;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginResult {

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;

    public static LoginResult from(AccessTokenResponse response) {
        return LoginResult.builder()
                .accessToken(response.getToken())
                .refreshToken(response.getRefreshToken())
                .expiresIn(response.getExpiresIn())
                .build();
    }
}