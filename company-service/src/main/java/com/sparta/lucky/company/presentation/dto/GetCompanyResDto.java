package com.sparta.lucky.company.presentation.dto;

import com.sparta.lucky.company.application.dto.GetCompanyResult;
import com.sparta.lucky.company.domain.CompanyType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetCompanyResDto {
    private final UUID id;
    private final String name;
    private final CompanyType companyType;
    private final UUID hubId;
    private final UUID manager;
    private final String address;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static GetCompanyResDto from(GetCompanyResult result) {
        return GetCompanyResDto.builder()
                .id(result.getId())
                .name(result.getName())
                .companyType(result.getCompanyType())
                .hubId(result.getHubId())
                .manager(result.getManager())
                .address(result.getAddress())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .build();
    }
}