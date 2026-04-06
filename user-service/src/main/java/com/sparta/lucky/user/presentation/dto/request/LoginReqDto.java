package com.sparta.lucky.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto {

    @NotBlank(message = "사용자명은 필수입니다")
    private String username;
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}