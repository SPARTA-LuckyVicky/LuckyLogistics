package com.sparta.lucky.product.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CreateProductCommand {
    private final UUID companyId;
    private final UUID hubId;
    private final String name;
    private final Integer price;
    private final Integer stock;

    // 소유권 검증용 — Gateway 헤더에서 추출
    private final UUID requesterId;
    private final String requesterRole;
    private final UUID requesterHubId;  // HUB_MANAGER: 자기 허브에만 생성 가능
}
