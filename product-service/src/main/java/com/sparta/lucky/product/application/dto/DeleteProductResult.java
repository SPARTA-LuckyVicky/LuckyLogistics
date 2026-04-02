package com.sparta.lucky.product.application.dto;

import com.sparta.lucky.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DeleteProductResult {
    private final UUID id;
    private final LocalDateTime deletedAt;
    private final UUID deletedBy;

    public static DeleteProductResult from(Product product) {
        return DeleteProductResult.builder()
                .id(product.getId())
                .deletedAt(product.getDeletedAt())
                .deletedBy(product.getDeletedBy())
                .build();
    }
}
