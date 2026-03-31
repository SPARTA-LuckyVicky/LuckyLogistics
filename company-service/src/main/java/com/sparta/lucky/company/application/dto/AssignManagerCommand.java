package com.sparta.lucky.company.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

// 내부 API — user-service가 담당자 배정 시 호출
@Getter
@Builder
public class AssignManagerCommand {
    private final UUID companyId;
    private final UUID managerId;
}