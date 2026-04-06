package com.sparta.lucky.product.presentation;

import com.sparta.lucky.product.application.ProductService;
import com.sparta.lucky.product.common.exception.BusinessException;
import com.sparta.lucky.product.common.response.ApiResponse;
import com.sparta.lucky.product.domain.ProductErrorCode;
import com.sparta.lucky.product.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Product Internal", description = "상품 내부 서비스 간 통신 API")
@RestController
@RequestMapping("/internal/api/v1/products")
@RequiredArgsConstructor
public class ProductInternalController {

    // 내부 요청 헤더값 상수화
    private static final String INTERNAL_REQUEST_VALUE = "true";

    private final ProductService productService;

    // 내부 API 헤더 검증 — X-Internal-Request: true 가 없으면 즉시 거절
    private void validateInternalRequest(String internalRequest) {
        if (!INTERNAL_REQUEST_VALUE.equals(internalRequest)) {
            throw new BusinessException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
        }
    }

    /**
     * 상품 단건 조회 (공용)
     */
    @Operation(summary = "[내부] 상품 단건 조회", description = "order 서비스가 상품 정보 조회 시 호출")
    @GetMapping("/{productId}")
    public ApiResponse<GetProductResDto> getProduct(
            @PathVariable UUID productId,
            @RequestHeader("X-Internal-Request") String internalRequest
    ) {
        validateInternalRequest(internalRequest);
        return ApiResponse.success(
                GetProductResDto.from(productService.getProductInternal(productId))
        );
    }

    /**
     * 재고 차감 (주문 생성 시 order-service 호출)
     */
    @Operation(summary = "[내부] 재고 차감", description = "order 서비스가 주문 생성 시 호출. 낙관적 락 적용 - 충돌 시 409 반환")
    @PatchMapping("/{productId}/stock/decrease")
    public ApiResponse<StockChangeResDto> decreaseStock(
            @PathVariable UUID productId,
            @Valid @RequestBody StockChangeReqDto reqDto,
            @RequestHeader("X-Internal-Request") String internalRequest
    ) {
        validateInternalRequest(internalRequest);
        return ApiResponse.success(
                StockChangeResDto.from(productService.decreaseStock(productId, reqDto.getQuantity()))
        );
    }

    /**
     * 재고 복원 (주문 취소 시 order-service 호출)
     */
    @Operation(summary = "[내부] 재고 복원", description = "order 서비스가 주문 취소 시 호출. " +
            "원래 수량을 그대로 전달하면 stock += quantity 처리")
    @PatchMapping("/{productId}/stock/restore")
    public ApiResponse<StockChangeResDto> restoreStock(
            @PathVariable UUID productId,
            @Valid @RequestBody StockChangeReqDto reqDto,
            @RequestHeader("X-Internal-Request") String internalRequest
    ) {
        validateInternalRequest(internalRequest);
        return ApiResponse.success(
                StockChangeResDto.from(productService.restoreStock(productId, reqDto.getQuantity()))
        );
    }

    /**
     * 업체 소속 상품 일괄 Soft Delete (업체 삭제 시 company-service 호출)
     */
    @Operation(summary = "[내부] 업체 소속 상품 일괄 삭제", description = "company 서비스가 업체 삭제 시 호출." +
            "소속 상품이 없어도 200 반환 (멱등성)")
    @DeleteMapping("/company/{companyId}")
    public ApiResponse<BulkDeleteResDto> deleteProductsByCompany(
            @PathVariable UUID companyId,
            @RequestHeader("X-Internal-Request") String internalRequest,
            @RequestHeader("X-User-Id") UUID deletedBy   // 감사 주체는 인증 헤더에서 추출 — 바디 위변조 방지
    ) {
        validateInternalRequest(internalRequest);
        return ApiResponse.success(
                BulkDeleteResDto.from(productService.deleteProductsByCompany(companyId, deletedBy))
        );
    }
}