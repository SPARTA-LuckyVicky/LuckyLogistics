package com.sparta.lucky.company.application.dto;

import com.sparta.lucky.company.domain.CompanyType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UpdateCompanyCommand {
    private final UUID companyId;

    // null이면 해당 필드 미변경
    private final String name;
    private final CompanyType companyType;
    private final String address;
    private final UUID hubId;  // MASTER만 변경 가능

    // JWT 파싱 데이터는 요청자 기준 > requester로 표기합니다
    // 소유권 검증용
    private final UUID requesterId;
    private final String requesterRole;
    private final UUID requesterHubId;     // HUB_MANAGER 검증
    private final UUID requesterCompanyId; // COMPANY_MANAGER 검증
}