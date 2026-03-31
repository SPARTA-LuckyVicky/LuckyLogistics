package com.sparta.lucky.company.common.exception;

// 각 도메인별 에러 코드 enum이 이 인터페이스를 구현
// 예: CompanyErrorCode implements ErrorCode
public interface ErrorCode {
    String getCode();       // "COMPANY_001"
    String getMessage();    // "업체를 찾을 수 없습니다."
    int getHttpStatus();    // 404
}