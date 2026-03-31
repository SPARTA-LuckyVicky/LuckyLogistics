package com.sparta.lucky.company.application.dto;

import com.sparta.lucky.company.domain.CompanyType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

// Controller → Service 전달 DTO (비즈니스 명령)
@Getter
@Builder
public class CreateCompanyCommand {
    private final String name;
    private final CompanyType companyType;
    private final UUID hubId;
    private final String address;

    // 소유권 검증용 — Gateway 헤더에서 추출
    private final UUID requesterId;
    private final String requesterRole;
    private final UUID requesterHubId;  // HUB_MANAGER: 자기 허브에만 생성 가능
}