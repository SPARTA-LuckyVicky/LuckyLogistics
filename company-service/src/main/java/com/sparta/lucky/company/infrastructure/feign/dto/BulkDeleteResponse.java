package com.sparta.lucky.company.infrastructure.feign.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

// product-service 일괄 삭제 응답 — 로깅용
@Getter
@NoArgsConstructor
public class BulkDeleteResponse {
    private UUID companyId;
    private int deletedCount;
    private LocalDateTime deletedAt;
}