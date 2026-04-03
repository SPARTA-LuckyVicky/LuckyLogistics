package com.sparta.lucky.order.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProductResponse {
    private UUID id;
    private UUID companyId;
    private UUID hubId;
    private String name;
    private Integer price;
    private Integer stock;
    private String status;
}