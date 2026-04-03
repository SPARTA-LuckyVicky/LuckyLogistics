package com.sparta.lucky.product.infrastructure.feign.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CompanyResponse {
    private UUID id;
    private UUID hubId;
    private String name;
    private String companyType;
    private UUID manager; // 업체 담당자 - COMPANY_MANAGER 소유권 검증시 사용
}