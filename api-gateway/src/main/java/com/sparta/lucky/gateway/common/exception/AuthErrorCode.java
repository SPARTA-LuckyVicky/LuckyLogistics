package com.sparta.lucky.gateway.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode{
    TOKEN_EXPIRED("AUTH_007", "만료된 토큰입니다.", 401),
    INVALID_TOKEN("AUTH_008", "유효하지 않은 토큰입니다.", 401),
    UNAUTHORIZED_ACCESS("AUTH_009", "접근 권한이 없습니다.", 403),
    TOKEN_NOT_FOUND("AUTH_010", "인증 토큰이 누락되었습니다.", 401);

    private final String code;
    private final String message;
    private final int httpStatus;
}
