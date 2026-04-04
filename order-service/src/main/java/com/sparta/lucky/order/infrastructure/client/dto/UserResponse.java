package com.sparta.lucky.order.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserResponse {
    private UUID userId;
    private String name;
    private String receiverSlackId;
    private UUID hubId;      // HUB_MANAGER 소속 허브
    private UUID companyId;  // COMPANY_MANAGER 소속 업체
}