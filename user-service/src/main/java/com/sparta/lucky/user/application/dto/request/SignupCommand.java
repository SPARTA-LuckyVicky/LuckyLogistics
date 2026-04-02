package com.sparta.lucky.user.application.dto.request;

import com.sparta.lucky.user.domain.UserRole;
import com.sparta.lucky.user.presentation.dto.request.SignupReqDto;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SignupCommand {
    private final String username;
    private final String password;
    private final String name;
    private final String receiverSlackId;
    private final UserRole role;
    private final UUID hubId;
    private final UUID companyId;

    @Builder
    private SignupCommand(String username, String password, String name, String receiverSlackId, UserRole role, UUID hubId, UUID companyId) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.receiverSlackId = receiverSlackId;
        this.role = role;
        this.hubId = hubId;
        this.companyId = companyId;
    }

    public static SignupCommand from(SignupReqDto reqDto, String encodedPassword){
        return SignupCommand.builder()
                .username(reqDto.getUsername())
                .password(encodedPassword)
                .name(reqDto.getName())
                .receiverSlackId(reqDto.getReceiverSlackId())
                .role(reqDto.getRole())
                .hubId(reqDto.getHubId())
                .companyId(reqDto.getCompanyId())
                .build();
    }
}
