package com.sparta.lucky.product.presentation.dto;

import com.sparta.lucky.product.application.dto.DeleteProductResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DeleteProductResDto {
    private final UUID id;
    private final LocalDateTime deletedAt;
    private final UUID deletedBy;

    public static DeleteProductResDto from(DeleteProductResult result) {
        return DeleteProductResDto.builder()
                .id(result.getId())
                .deletedAt(result.getDeletedAt())
                .deletedBy(result.getDeletedBy())
                .build();
    }
}