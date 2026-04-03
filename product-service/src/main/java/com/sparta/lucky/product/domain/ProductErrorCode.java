package com.sparta.lucky.product.domain;

import com.sparta.lucky.product.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    COMPANY_NOT_FOUND("PRODUCT_001", "해당 업체를 찾을 수 없습니다.", 404),
    HUB_NOT_FOUND("PRODUCT_002", "해당 허브를 찾을 수 없습니다.", 404),
    PRODUCT_ACCESS_DENIED("PRODUCT_003", "본인 업체의 상품이 아닙니다.", 403),
    PRODUCT_NOT_ALLOWED("PRODUCT_004", "담당 허브의 상품이 아닙니다.", 403),
    PRODUCT_NOT_FOUND("PRODUCT_005", "해당 상품을 찾을 수 없습니다.", 404),
    STOCK_NOT_ENOUGH("PRODUCT_006", "재고가 부족합니다.", 409),
    STOCK_CONFLICT("PRODUCT_007", "재고 동시 수정 충돌 발생. 다시 시도해주세요.", 409),
    // 차감/복원 요청 수량이 0 이하인 경우 — 음수 수량으로 인한 재고 증가·감소 버그 방지
    INVALID_STOCK_QUANTITY("PRODUCT_008", "수량은 1 이상이어야 합니다.", 400),
    // 재고 복원 시 int 최대값 초과 방지 - currentStock + quantity > Integer.MAX_VALUE
    STOCK_OVERFLOW("PRODUCT_009", "재고가 최대값을 초과합니다.", 400);

    private final String code;
    private final String message;
    private final int httpStatus;
}
