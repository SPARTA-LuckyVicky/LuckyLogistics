package com.sparta.lucky.product.application.dto;

import com.sparta.lucky.product.domain.Product;
import com.sparta.lucky.product.domain.ProductStatus;
import com.sparta.lucky.product.domain.ProductStock;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateProductResult {
    private final UUID id;
    private final UUID companyId;
    private final UUID hubId;
    private final String name;
    private final Integer price;
    private final ProductStatus status;
    private final Integer stock;
    private final LocalDateTime createdAt;
    private final UUID createdBy;

    // Entity → Result 변환 팩토리 메서드
    public static CreateProductResult from(Product product, ProductStock stock) {
        return CreateProductResult.builder()
                .id(product.getId())
                .companyId(product.getCompanyId())
                .hubId(product.getHubId())
                .name(product.getName())
                .price(product.getPrice())
                .status(product.getStatus())
                .stock(stock.getStock())
                .createdAt(product.getCreatedAt())
                .createdBy(product.getCreatedBy())
                .build();
    }
}
