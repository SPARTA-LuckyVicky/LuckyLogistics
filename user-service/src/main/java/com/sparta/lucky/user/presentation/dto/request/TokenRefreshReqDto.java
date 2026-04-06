package com.sparta.lucky.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshReqDto {

    @NotBlank(message = "refreshToken은 필수입니다.")
    private String refreshToken;

}
