package com.sparta.lucky.user.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode{

    // 가입 및 인증 관련
    USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다.", 404),
    DUPLICATE_USERNAME("USER_002", "이미 존재하는 아이디입니다.", 400),
    EXTERNAL_AUTH_ERROR("USER_003", "인증 서버와의 통신 중 에러가 발생했습니다.", 500),

    // role 및 소속 관련 검증용
    INVALID_HUB_ID("USER_004", "허브 관리자는 소속 허브 ID가 필수입니다.", 400),
    INVALID_COMPANY_ID("USER_005", "업체 담당자는 소속 업체 ID가 필수입니다.", 400),

    // status (가입 상태) 관련
    UNAUTHORIZED_ACCESS("USER_006", "승인되지 않은 사용자입니다.", 403),

    // 권한 관련
    FORBIDDEN_ACCESS("USER_007", "해당 작업에 대한 권한이 없습니다.", 403),

    // 유저 상태 관련
    DELETED_USER("USER_008", "이미 탈퇴 처리된 사용자입니다.", 400),

    // 데이터 무결성
    IMMUTABLE_FIELD("USER_009", "변경할 수 없는 정보가 포함되어 있습니다.", 400),

    // 서버 내부 에러 (Keycloak 등 외부 연동 실패 상세)
    KEYCLOAK_USER_CREATION_FAILED("USER_011", "인증 서버 유저 생성에 실패했습니다.", 500),

    LOGIN_FAILED("USER_012", "로그인에 실패했습니다.", 400),
    LOGOUT_FAILED("USER_013", "로그아웃에 실패했습니다.", 400),
    INVALID_STATUS_CHANGE("USER_014", "변경할 수 없는 유저 상태입니다.", 400),
    INVALID_REFRESH_TOKEN("USER_015", "유효하지 않는 리프레시 토큰 입니다.",400 );



    private final String code;
    private final String message;
    private final int httpStatus;
}
