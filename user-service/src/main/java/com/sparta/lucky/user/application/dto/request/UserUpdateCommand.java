package com.sparta.lucky.user.application.dto.request;

import com.sparta.lucky.user.presentation.dto.request.UserUpdateReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserUpdateCommand {
    private UUID userId;
    private String role; // Gateway에서 온 역할
    private String name;
    private String receiverSlackId;
    private String hubId;
    private String companyId;

    public static UserUpdateCommand of(UUID userId, String role, String hubId,
                                       String companyId, UserUpdateReqDto reqDto) {
        return UserUpdateCommand.builder()
                .userId(userId)
                .role(role)
                .name(reqDto.getName())
                .receiverSlackId(reqDto.getReceiverSlackId())
                .hubId(hubId)
                .companyId(companyId)
                .build();
    }
}