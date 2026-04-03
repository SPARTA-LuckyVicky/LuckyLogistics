package com.sparta.lucky.product.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

// 내부 API - 재고 차감용 dto

@Getter
public class StockChangeReqDto {

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private Integer quantity;
}