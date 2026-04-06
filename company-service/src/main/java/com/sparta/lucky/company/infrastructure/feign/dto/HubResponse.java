package com.sparta.lucky.company.infrastructure.feign.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

// hub-service 내부 API 응답 - 허브 실존 검증에 필요한 필드만 받아옴
@Getter
@NoArgsConstructor
public class HubResponse {
    private UUID hubId;
    private String name;
}