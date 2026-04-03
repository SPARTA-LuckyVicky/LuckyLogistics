package com.sparta.lucky.order.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND("ORD_001", "존재하지 않는 주문입니다.", 404),
    INVALID_ORDER_STATUS("ORD_002", "유효하지 않은 주문 상태입니다.", 409),

    ORDER_CANNOT_BE_DELETED("ORD_003", "진행 중인 주문은 삭제할 수 없습니다. 주문이 취소 또는 완료 상태만 가능 합니다.", 400),
    ORDER_CANNOT_BE_MODIFIED("ORD_004", "완료되거나 취소된 주문은 수정할 수 없습니다.", 400),

    ORDER_INVALID_QUANTITY("ORD_005", "수량의 범위가 잘못 되었습니다. 수량은 1개 이상 이어야 합니다.", 400),
    ORDER_INVALID_PRICE("ORD_006", "단가의 범위가 잘못 되었습니다. 단가는 1 이상 이어야 합니다.", 400),

    ORDER_ACCESS_DENIED ("ORD_007", "해당 주문에 대한 권한이 없습니다.", 403),

    OUT_OF_STOCK ("ORD_008", "재고가 부족하여 주문할 수 없습니다.", 409),
    PRODUCT_NOT_FOUND ("ORD_009", "존재하지 않는 상품입니다.", 404),

    ;

    private final String code;
    private final String message;
    private final int httpStatus;
}
