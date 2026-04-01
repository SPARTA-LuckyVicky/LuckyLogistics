package com.sparta.lucky.notification.common.exception;

import lombok.Getter;

import java.util.Objects;

// 도메인 비즈니스 규칙 위반 시 던지는 예외
// GlobalExceptionHandler가 잡아서 ApiResponse.error(...)로 변환
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;

    public BusinessException(ErrorCode errorCode) {
        super(Objects.requireNonNull(errorCode, "errorCode must not be null").getMessage());
        this.errorCode = errorCode.getCode();
        this.httpStatus = errorCode.getHttpStatus();
    }
}