package com.sparta.lucky.product.application.dto;


import com.sparta.lucky.product.domain.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UpdateProductCommand {

    private final UUID productId;

    // null이면 해당 필드 미변경
    private final String name;
    private final Integer price;
    private final ProductStatus status;
    private final UUID companyId;
    private final UUID hubId;

    // JWT 파싱 데이터는 요청자 기준 > requester로 표기합니다
    // 소유권 검증용
    private final UUID requesterId;
    private final String requesterRole;
    private final UUID requesterHubId;     // HUB_MANAGER 검증
    private final UUID requesterCompanyId; // COMPANY_MANAGER 검증


}
