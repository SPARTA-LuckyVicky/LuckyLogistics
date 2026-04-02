package com.sparta.lucky.product.presentation.dto;

import com.sparta.lucky.product.application.dto.GetProductResult;
import com.sparta.lucky.product.domain.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetProductResDto {
    private final UUID id;
    private final UUID companyId;
    private final UUID hubId;
    private final String name;
    private final Integer price;
    private final ProductStatus status;
    private final Integer stock;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static GetProductResDto from(GetProductResult result) {
        return GetProductResDto.builder()
                .id(result.getId())
                .companyId(result.getCompanyId())
                .hubId(result.getHubId())
                .name(result.getName())
                .price(result.getPrice())
                .status(result.getStatus())
                .stock(result.getStock())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }
}