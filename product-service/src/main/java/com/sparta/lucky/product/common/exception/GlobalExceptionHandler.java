package com.sparta.lucky.product.common.exception;

import com.sparta.lucky.product.common.response.ApiResponse;
import com.sparta.lucky.product.domain.ProductErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외 (404 조회 결과 없음, 403 권한 없음 등)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("[BusinessException] code={}, message={}", e.getErrorCode(), e.getMessage());
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    // @Valid 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("유효성 검증 실패");
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("VALIDATION_001", message));
    }

    // Enum, UUID 등 타입 변환 실패 (@RequestParam, @PathVariable)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = e.getName() + "의 값이 올바르지 않습니다: " + e.getValue();
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("VALIDATION_002", message));
    }

    // 헤더 누락 (MissingRequestHeaderException) / 요청 바디 파싱 실패 (HttpMessageNotReadableException) - 400
    @ExceptionHandler({MissingRequestHeaderException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e) {
        log.warn("[BadRequest] type={}", e.getClass().getSimpleName());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("VALIDATION_003", "요청 형식이 올바르지 않습니다."));
    }

    // 낙관적 락 충돌 — 동시 재고 차감 시 발생, order-service 재시도 필요 - 409
    // 에러 코드/메시지는 ProductErrorCode.STOCK_CONFLICT에서 단일 관리 (하드코딩 방지)
    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(
            org.springframework.orm.ObjectOptimisticLockingFailureException e) {
        log.warn("[OptimisticLock] 재고 동시 수정 충돌 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        ProductErrorCode.STOCK_CONFLICT.getCode(),
                        ProductErrorCode.STOCK_CONFLICT.getMessage()));
    }

    // 도메인 불변식 위반 (예: ProductStock.updateStock에 음수 입력) — 400
    // 서비스 레이어에서 사전 검증하므로 실제 발생 가능성은 낮지만 API 계약 일관성을 위해 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("[IllegalArgument] {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("VALIDATION_004", e.getMessage()));
    }

    // 그 외 예상치 못한 서버 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[Unexpected Error]", e);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_001", "서버 오류가 발생했습니다."));
    }
}