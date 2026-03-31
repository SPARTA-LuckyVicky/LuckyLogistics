package com.sparta.lucky.hub.common.exception;

public interface ErrorCode {
    String getCode();
    String getMessage();
    int getHttpStatus();
}