package com.sparta.lucky.user.presentation.dto.response;

import com.sparta.lucky.user.application.dto.response.UserResult;
import com.sparta.lucky.user.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResDto {
    private String username;
    private String name;
    private String role;
    private String receiverSlackId;
    private UserStatus status;
    private String hubId;
    private String companyId;

    public static UserResDto from(UserResult result) {
        return UserResDto.builder()
                .username(result.getUsername())
                .name(result.getName())
                .role(result.getRole())
                .receiverSlackId(result.getReceiverSlackId())
                .status(result.getStatus())
                .hubId(result.getHubId())
                .companyId(result.getCompanyId())
                .build();
    }
}