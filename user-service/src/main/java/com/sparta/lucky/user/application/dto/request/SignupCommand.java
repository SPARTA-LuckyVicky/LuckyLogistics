package com.sparta.lucky.user.application.dto.request;

import com.sparta.lucky.user.domain.User;
import com.sparta.lucky.user.domain.UserRole;
import com.sparta.lucky.user.domain.UserStatus;
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
    private final String hubId;
    private final String companyId;

    @Builder
    private SignupCommand(String username, String password, String name, String receiverSlackId, UserRole role, String hubId, String companyId) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.receiverSlackId = receiverSlackId;
        this.role = role;
        this.hubId = hubId;
        this.companyId = companyId;
    }

    public static SignupCommand from(SignupReqDto reqDto){
        return SignupCommand.builder()
                .username(reqDto.getUsername())
                .password(reqDto.getPassword())
                .name(reqDto.getName())
                .receiverSlackId(reqDto.getReceiverSlackId())
                .role(reqDto.getRole())
                .hubId(reqDto.getHubId())
                .companyId(reqDto.getCompanyId())
                .build();
    }

    public User toEntity(UUID keycloakId, String encodedPassword) {
        return User.builder()
                .userId(keycloakId)
                .username(this.username)
                .password(encodedPassword) // 암호화된 비밀번호 사용
                .name(this.name)
                .receiverSlackId(this.receiverSlackId)
                .role(this.role)
                .hubId(this.hubId)
                .companyId(this.companyId)
                .status(UserStatus.PENDING) // 가입 시 기본 상태는 PENDING
                .build();
    }
}
