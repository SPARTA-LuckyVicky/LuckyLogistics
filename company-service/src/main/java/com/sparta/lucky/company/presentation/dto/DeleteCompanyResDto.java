package com.sparta.lucky.company.presentation.dto;

import com.sparta.lucky.company.application.dto.DeleteCompanyResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DeleteCompanyResDto {
    private final LocalDateTime deletedAt;
    private final UUID deletedBy;

    public static DeleteCompanyResDto from(DeleteCompanyResult result) {
        return DeleteCompanyResDto.builder()
                .deletedAt(result.getDeletedAt())
                .deletedBy(result.getDeletedBy())
                .build();
    }
}