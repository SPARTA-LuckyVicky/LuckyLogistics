package com.sparta.lucky.product.presentation;

import com.sparta.lucky.product.application.ProductService;
import com.sparta.lucky.product.application.dto.CreateProductCommand;
import com.sparta.lucky.product.application.dto.DeleteProductCommand;
import com.sparta.lucky.product.application.dto.UpdateProductCommand;
import com.sparta.lucky.product.application.dto.UpdateProductStockCommand;
import com.sparta.lucky.product.common.exception.BusinessException;
import com.sparta.lucky.product.common.response.ApiResponse;
import com.sparta.lucky.product.domain.ProductErrorCode;
import com.sparta.lucky.product.domain.ProductStatus;
import com.sparta.lucky.product.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Product", description = "상품 관리 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 생성
     * [권한]
     * - MASTER : 모두 가능
     * - HUB_MANAGER : 담당 허브 소속 상품만 가능
     * - COMPANY_MANAGER : 본인 업체 상품만 가능
     */
    @Operation(summary = "상품 생성", description = "새로운 상품을 생성합니다. (MASTER, HUB_MANAGER, COMPANY_MANAGER)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostProductResDto> createProduct(
            @Valid @RequestBody PostProductReqDto reqDto,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
            ) {
        validateHubHeader(userRole, hubId);
        CreateProductCommand command = CreateProductCommand.builder()
                .companyId(reqDto.getCompanyId())
                .hubId(reqDto.getHubId())
                .name(reqDto.getName())
                .price(reqDto.getPrice())
                .stock(reqDto.getStock())
                .requesterId(userId)
                .requesterRole(userRole)
                .requesterHubId(hubId)
                .build();

        return ApiResponse.success(
                PostProductResDto.from(productService.createProduct(command))
        );
    }

    /**
     * 상품 목록 조회 / 검색
     * [권한]
     * - 전체 로그인 사용자 가능
     * - HUB_MANAGER: 담당 허브 소속 상품만 조회
     */
    @Operation(summary = "상품 목록 조회", description = "name(부분 검색), status, companyId 필터 가능." +
            "페이지 사이즈: 10 / 30 / 50 (그 외는 10으로 고정). HUB_MANAGER는 담당 허브 상품만 조회됩니다.")
    @GetMapping
    public ApiResponse<Page<GetProductResDto>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) UUID companyId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        validateHubHeader(userRole, hubId);

        int validSize = List.of(10, 30, 50).contains(pageable.getPageSize())
                ? pageable.getPageSize() : 10;
        Pageable validPageable = PageRequest.of(pageable.getPageNumber(), validSize, pageable.getSort());

        return ApiResponse.success(
                productService.getProducts(name, status, companyId, userRole, hubId, validPageable)
                        .map(GetProductResDto::from)
        );
    }

    /**
     * 상품 단건 조회
     * [권한]
     * - 전체 로그인 사용자 가능
     * - HUB_MANAGER: 담당 허브 소속 상품만 조회
     */
    @Operation(summary = "상품 단건 조회", description = "HUB_MANAGER는 담당 허브 소속 상품만 조회 가능합니다.")
    @GetMapping("/{productId}")
    public ApiResponse<GetProductResDto> getProduct(
            @PathVariable UUID productId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        // HUB_MANAGER가 X-Hub-Id 없이 단건 조회하면 서비스 레이어에서 NPE 발생 가능하므로 검증
        validateHubHeader(userRole, hubId);
        return ApiResponse.success(
                GetProductResDto.from(productService.getProduct(productId, userRole, hubId))
        );
    }

    /**
     * 상품 수정 (재고 제외)
     * [권한]
     * - MASTER : 모두 가능
     * - HUB_MANAGER : 담당 허브 소속 상품만 가능
     * - COMPANY_MANAGER : 본인 업체 상품만 가능
     */
    @Operation(summary = "상품 수정", description = "재고를 제외한 상품 기본 정보를 수정합니다. companyId / hubId 변경은 MASTER만 가능." +
            "(MASTER, HUB_MANAGER, COMPANY_MANAGER)")
    @PatchMapping("/{productId}")
    public ApiResponse<GetProductResDto> updateProductWithoutStock(
            @PathVariable UUID productId,
            @Valid @RequestBody PatchProductReqDto reqDto,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId,
            @RequestHeader(value = "X-Company-Id", required = false) UUID companyIdHeader
    ) {
        validateHubHeader(userRole, hubId);
        UpdateProductCommand command = UpdateProductCommand.builder()
                .productId(productId)
                .name(reqDto.getName())
                .price(reqDto.getPrice())
                .status(reqDto.getStatus())
                .companyId(reqDto.getCompanyId())
                .hubId(reqDto.getHubId())
                .requesterId(userId)
                .requesterRole(userRole)
                .requesterHubId(hubId)
                .requesterCompanyId(companyIdHeader)
                .build();

        return ApiResponse.success(
                GetProductResDto.from(productService.updateProduct(command))
        );

    }

    /**
     * 재고 수정
     * [권한]
     * - MASTER : 모두 가능
     * - HUB_MANAGER : 담당 허브 소속 상품만 가능
     */
    @Operation(summary = "재고 수정", description = "상품 재고를 절대값으로 수정합니다. (MASTER, HUB_MANAGER)")
    @PatchMapping("/{productId}/stock")
    public ApiResponse<GetProductResDto> updateStock(
            @PathVariable UUID productId,
            @Valid @RequestBody PatchProductStockReqDto reqDto,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        validateHubHeader(userRole, hubId);
        UpdateProductStockCommand command = UpdateProductStockCommand.builder()
                .productId(productId)
                .stock(reqDto.getStock())
                .requesterId(userId)
                .requesterRole(userRole)
                .requesterHubId(hubId)
                .build();

        return ApiResponse.success(
                GetProductResDto.from(productService.updateStock(command))
        );
    }

    /**
     * 상품 삭제 (Soft Delete)
     * [권한]
     * - MASTER : 모두 가능
     * - HUB_MANAGER : 담당 허브 소속 상품만 가능
     */
    @Operation(summary = "상품 삭제", description = "상품과 재고를 소프트 삭제합니다. (MASTER, HUB_MANAGER)")
    @DeleteMapping("/{productId}")
    public ApiResponse<DeleteProductResDto> deleteProduct(
            @PathVariable UUID productId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        validateHubHeader(userRole, hubId);
        DeleteProductCommand command = DeleteProductCommand.builder()
                .productId(productId)
                .requesterId(userId)
                .requesterRole(userRole)
                .requesterHubId(hubId)
                .build();

        return ApiResponse.success(
                DeleteProductResDto.from(productService.deleteProduct(command))
        );
    }

    // HUB_MANAGER 역할인데 X-Hub-Id 헤더가 없으면 즉시 거절 — 서비스 레이어 NPE 방지
    private static final String ROLE_HUB_MANAGER = "HUB_MANAGER";
    private void validateHubHeader(String userRole, UUID hubId) {
        if (ROLE_HUB_MANAGER.equals(userRole) && hubId == null) {
            throw new BusinessException(ProductErrorCode.PRODUCT_NOT_ALLOWED);
        }
    }


}
