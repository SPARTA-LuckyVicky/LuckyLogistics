package com.sparta.lucky.company.application.dto;

import com.sparta.lucky.company.domain.Company;
import com.sparta.lucky.company.domain.CompanyType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CreateCompanyResult {
    private final UUID id;
    private final String name;
    private final CompanyType companyType;
    private final UUID hubId;
    private final UUID manager;
    private final String address;
    private final LocalDateTime createdAt;
    private final UUID createdBy;

    // Entity → Result 변환 팩토리 메서드
    public static CreateCompanyResult from(Company company) {
        return CreateCompanyResult.builder()
                .id(company.getId())
                .name(company.getName())
                .companyType(company.getCompanyType())
                .hubId(company.getHubId())
                .manager(company.getManager())
                .address(company.getAddress())
                .createdAt(company.getCreatedAt())
                .createdBy(company.getCreatedBy())
                .build();
    }
}