package com.sparta.lucky.order.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND("ORD_001", "존재하지 않는 주문입니다.", 404),
    INVALID_ORDER_STATUS("ORD_002", "유효하지 않은 주문 상태입니다.", 409),
    ORDER_CANNOT_BE_DELETED("ORD_003", "진행 중인 주문은 삭제할 수 없습니다. 먼저 취소해주세요.", 409),
    ORDER_CANNOT_BE_MODIFIED("ORD_004", "완료되거나 취소된 주문은 수정할 수 없습니다.", 409),
    ORDER_INVALID_PRICE("ORD_005", "가격의 범위가 잘못되었습니다..", 409),
    ORDER_INVALID_QUANTITY("ORD_006", "수량은 1개 이상이어야 합니다.", 409);

    private final String code;
    private final String message;
    private final int httpStatus;
}
