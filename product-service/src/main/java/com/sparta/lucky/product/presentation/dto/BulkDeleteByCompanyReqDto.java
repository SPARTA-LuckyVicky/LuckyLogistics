package com.sparta.lucky.product.presentation.dto;

// 내부 API - 업체 삭제시 상품 + 재고 레코드 일괄 삭제처리용
// deletedBy는 요청 바디가 아닌 X-User-Id 헤더에서 추출 - 클라이언트 위변조 방지
public class BulkDeleteByCompanyReqDto {
    // 현재 바디 없음 — 확장 시 추가
}
