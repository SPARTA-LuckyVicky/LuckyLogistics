package com.sparta.lucky.product.application.dto;

import com.sparta.lucky.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class StockChangeResult {
    private final UUID productId;
    private final Integer stock;

    public static StockChangeResult from(Product product) {
        return StockChangeResult.builder()
                .productId(product.getId())
                .stock(product.getStock().getStock())
                .build();
    }
}