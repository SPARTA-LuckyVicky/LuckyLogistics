package com.sparta.lucky.hub.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HubErrorCode implements ErrorCode {

    HUB_NOT_FOUND("HUB_001", "허브를 찾을 수 없습니다.", 404),
    HUB_ALREADY_DELETED("HUB_002", "이미 삭제된 허브입니다.", 400),
    HUB_ROUTE_INVALID_VALUE("HUB_003", "distance와 duration은 음수일 수 없습니다.", 400),
    HUB_ROUTE_NOT_FOUND("HUB_004", "두 허브 간 경로를 찾을 수 없습니다.", 404),
    INVALID_COORDINATE("HUB_005", "대한민국 범위를 벗어난 좌표입니다. (위도 33~39, 경도 124~132)", 400),
    HUB_ACCESS_DENIED("HUB_006", "내부 서비스 요청만 허용됩니다.", 403);

    private final String code;
    private final String message;
    private final int httpStatus;
}