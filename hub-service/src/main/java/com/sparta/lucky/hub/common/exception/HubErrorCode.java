package com.sparta.lucky.hub.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HubErrorCode implements ErrorCode {

    HUB_NOT_FOUND("HUB_001", "허브를 찾을 수 없습니다.", 404),
    HUB_ALREADY_DELETED("HUB_002", "이미 삭제된 허브입니다.", 400);

    private final String code;
    private final String message;
    private final int httpStatus;
}