package com.sparta.lucky.user.presentation.dto.response;

import com.sparta.lucky.user.application.dto.response.SignupResult;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder

public class SignupResDto {
    private final UUID userId;
    private final String username;

    public static SignupResDto from(SignupResult result) {
        return SignupResDto.builder()
                .userId(result.getUserId())
                .username(result.getUsername())
                .build();
    }
}
