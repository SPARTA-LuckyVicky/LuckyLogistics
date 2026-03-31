package com.sparta.lucky.gateway.common.exception;

public interface ErrorCode {

    String getCode();
    String getMessage();
    int getHttpStatus();
}
