package com.sparta.lucky.company.application.dto;

import com.sparta.lucky.company.domain.Company;
import com.sparta.lucky.company.domain.CompanyType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetCompanyResult {
    private final UUID id;
    private final String name;
    private final CompanyType companyType;
    private final UUID hubId;
    private final UUID manager;
    private final String address;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static GetCompanyResult from(Company company) {
        return GetCompanyResult.builder()
                .id(company.getId())
                .name(company.getName())
                .companyType(company.getCompanyType())
                .hubId(company.getHubId())
                .manager(company.getManager())
                .address(company.getAddress())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }
}