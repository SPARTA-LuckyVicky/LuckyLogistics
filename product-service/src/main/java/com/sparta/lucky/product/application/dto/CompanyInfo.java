package com.sparta.lucky.product.application.dto;

import com.sparta.lucky.product.infrastructure.feign.dto.CompanyResponse;

import java.util.UUID;

public record CompanyInfo(UUID id, UUID hubId, UUID manager) {

    // Infrastructure DTO → Application Info로 변환
    // Application에 Infrastructure DTO를 그대로 전달하는 것을 방지
    public static CompanyInfo from(CompanyResponse response) {
        return new CompanyInfo(response.getId(), response.getHubId(), response.getManager());
    }
}