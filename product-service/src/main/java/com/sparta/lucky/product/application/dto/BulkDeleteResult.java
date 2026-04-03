package com.sparta.lucky.product.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class BulkDeleteResult {
    private final UUID companyId;
    private final int deletedCount;
    private final LocalDateTime deletedAt;
}