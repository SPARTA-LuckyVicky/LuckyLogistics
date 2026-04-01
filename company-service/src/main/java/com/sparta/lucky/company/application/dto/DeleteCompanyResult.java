package com.sparta.lucky.company.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DeleteCompanyResult {
    private final LocalDateTime deletedAt;
    private final UUID deletedBy;

    public static DeleteCompanyResult of(LocalDateTime deletedAt, UUID deletedBy) {
        return DeleteCompanyResult.builder()
                .deletedAt(deletedAt)
                .deletedBy(deletedBy)
                .build();
    }
}