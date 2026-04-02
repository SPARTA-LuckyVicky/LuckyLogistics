package com.sparta.lucky.product.application.dto;

import com.sparta.lucky.product.domain.Product;
import com.sparta.lucky.product.domain.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetProductResult {
    private final UUID id;
    private final UUID companyId;
    private final UUID hubId;
    private final String name;
    private final Integer price;
    private final ProductStatus status;
    private final Integer stock;           // stock 포함
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static GetProductResult from(Product product) {
        return GetProductResult.builder()
                .id(product.getId())
                .companyId(product.getCompanyId())
                .hubId(product.getHubId())
                .name(product.getName())
                .price(product.getPrice())
                .status(product.getStatus())
                .stock(product.getStock().getStock()) // JOIN FETCH로 이미 로딩됨
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}