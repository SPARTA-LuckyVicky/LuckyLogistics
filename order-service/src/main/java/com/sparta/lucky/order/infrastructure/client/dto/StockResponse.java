package com.sparta.lucky.order.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class StockResponse {
    private UUID productId;
    private Integer stock;
}