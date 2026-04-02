package com.sparta.lucky.user.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{

    // 가입 및 인증 관련
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다.", 404),
    DUPLICATE_USERNAME("USER_002", "이미 존재하는 아이디입니다.", 400),

    // role 및 소속 관련 검증용
    INVALID_HUB_ID("USER_004", "허브 관리자는 소속 허브 ID가 필수입니다.", 400),
    INVALID_COMPANY_ID("USER_005", "업체 담당자는 소속 업체 ID가 필수입니다.", 400),

    // status 관련
    UNAUTHORIZED_ACCESS("USER_006", "승인되지 않은 사용자입니다.", 403);


    private final String code;
    private final String message;
    private final int httpStatus;
}
