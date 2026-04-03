package com.sparta.lucky.product.presentation.dto;

import com.sparta.lucky.product.application.dto.BulkDeleteResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class BulkDeleteResDto {
    private final UUID companyId;
    private final int deletedCount;
    private final LocalDateTime deletedAt;

    public static BulkDeleteResDto from(BulkDeleteResult result) {
        return BulkDeleteResDto.builder()
                .companyId(result.getCompanyId())
                .deletedCount(result.getDeletedCount())
                .deletedAt(result.getDeletedAt())
                .build();
    }
}