package com.sparta.lucky.product.presentation.dto;

import com.sparta.lucky.product.application.dto.StockChangeResult;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class StockChangeResDto {
    private final UUID productId;
    private final Integer stock;

    public static StockChangeResDto from(StockChangeResult result) {
        return StockChangeResDto.builder()
                .productId(result.getProductId())
                .stock(result.getStock())
                .build();
    }
}