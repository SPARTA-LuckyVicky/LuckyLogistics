package com.sparta.lucky.company.domain;

import com.sparta.lucky.company.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyErrorCode implements ErrorCode {

    COMPANY_NOT_FOUND("COMPANY_001", "업체를 찾을 수 없습니다.", 404),
    COMPANY_ACCESS_DENIED("COMPANY_002", "해당 업체에 접근 권한이 없습니다.", 403),
    COMPANY_HUB_MISMATCH("COMPANY_003", "허브 관리자는 소속 허브의 업체만 관리할 수 있습니다.", 403),
    COMPANY_ALREADY_DELETED("COMPANY_004", "이미 삭제된 업체입니다.", 409);

    private final String code;
    private final String message;
    private final int httpStatus;
}