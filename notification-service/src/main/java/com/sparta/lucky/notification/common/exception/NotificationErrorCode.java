package com.sparta.lucky.notification.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    ORDER_NOT_FOUND("ORD_001", "존재하지 않는 주문입니다.", 404),
    INVALID_ORDER_STATUS("ORD_002", "유효하지 않은 주문 상태입니다.", 409);

    private final String code;
    private final String message;
    private final int httpStatus;
}
