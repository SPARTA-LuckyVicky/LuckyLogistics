package com.sparta.lucky.user.application.dto.response;

import com.sparta.lucky.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class SignupResult {
    private final UUID userId;
    private final String username;
    public static SignupResult from (User user){
        return SignupResult.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();
    }
}
