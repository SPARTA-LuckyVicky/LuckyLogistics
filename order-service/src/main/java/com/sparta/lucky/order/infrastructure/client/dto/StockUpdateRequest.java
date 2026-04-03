package com.sparta.lucky.order.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StockUpdateRequest {
    private Integer quantity;
}