package com.sparta.lucky.product.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

// 내부 API - 업체 삭제시 상품 + 재고 레코드 일괄 삭제처리용

@Getter
public class BulkDeleteByCompanyReqDto {

    @NotNull(message = "deletedBy는 필수입니다.")
    private UUID deletedBy;
}