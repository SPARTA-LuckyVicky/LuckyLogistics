package com.sparta.lucky.user.presentation.dto.request;

import com.sparta.lucky.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class SignupReqDto {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 4, max = 10, message = "아이디는 4자 이상, 10자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 알파벳 소문자와 숫자로만 구성되어야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상, 15자 이하로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자를 각각 최소 1개 이상 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(max = 50)
    private String name;

    @NotBlank(message = "슬랙 ID는 필수 입력 값입니다.")
    private String receiverSlackId;

    @NotNull(message = "권한 선택은 필수입니다.")
    private UserRole role;

    private String hubId;

    private String companyId;
}
