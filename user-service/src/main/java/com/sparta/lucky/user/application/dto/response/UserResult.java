package com.sparta.lucky.user.application.dto.response;

import com.sparta.lucky.user.domain.User;
import com.sparta.lucky.user.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserResult {
    private UUID userId;
    private String username;
    private String name;
    private String role;
    private String receiverSlackId;
    private UserStatus status;
    private String hubId;
    private String companyId;

    public static UserResult from(User user) {
        return UserResult.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .receiverSlackId(user.getReceiverSlackId())
                .status(user.getStatus())
                .hubId(user.getHubId())
                .companyId(user.getCompanyId())
                .build();
    }
}