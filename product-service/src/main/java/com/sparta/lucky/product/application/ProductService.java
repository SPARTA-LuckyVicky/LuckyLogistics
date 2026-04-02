package com.sparta.lucky.product.application;

import com.sparta.lucky.product.application.dto.*;
import com.sparta.lucky.product.common.exception.BusinessException;
import com.sparta.lucky.product.domain.*;
import com.sparta.lucky.product.infrastructure.feign.CompanyClient;
import com.sparta.lucky.product.infrastructure.feign.HubClient;
import com.sparta.lucky.product.infrastructure.feign.dto.CompanyResponse;
import com.sparta.lucky.product.infrastructure.feign.dto.HubResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    // 매직스트링 상수화
    private static final String ROLE_MASTER = "MASTER";
    private static final String ROLE_HUB_MANAGER = "HUB_MANAGER";
    private static final String ROLE_COMPANY_MANAGER = "COMPANY_MANAGER";

    // 내부 요청 헤더값 상수화
    private static final String INTERNAL_REQUEST = "true";

    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    private final CompanyClient companyClient;
    private final HubClient hubClient;

    @Transactional
    public CreateProductResult createProduct(CreateProductCommand command) {
        log.info("상품 생성 요청 - requester: {}, role: {}, companyId: {}, hubId: {}, name: {}, price: {}, stock: {}",
                command.getRequesterId(), command.getRequesterRole(), command.getCompanyId(), command.getHubId()
        , command.getName(), command.getPrice(), command.getStock());

        // 업체 실존 검증 - company-service 내부 API 호출
        CompanyInfo company = validateCompany(command.getCompanyId());

        // 허브 실존 검증 - hub-service 내부 API 호출
        validateHub(command.getHubId());

        // 권한 검증 (내부 메서드)
        validateAuthority(
                command.getRequesterId(),
                command.getRequesterRole(),
                command.getRequesterHubId(),
                command.getHubId(),
                company);

        // Product와 ProductStock을 나누어 작성 및 저장
        Product product = Product.builder()
                .companyId(command.getCompanyId())
                .hubId(command.getHubId())
                .name(command.getName())
                .price(command.getPrice())
                .status(ProductStatus.ACTIVE)
                .build();
        productRepository.save(product);

        // Product 저장 후 생성된 Id를 바탕으로 ProductStock 생성 및 저장
        // 같은 트랜잭션 - product 저장 실패시 함께 롤백
        ProductStock stock = ProductStock.builder()
                .product(product)
                .hubId(product.getHubId())
                .stock(command.getStock())
                .version(0L)
                .build();
        productStockRepository.save(stock);

        //응답에 Product 정보, stock 정보를 함께 담아서 반환
        return CreateProductResult.from(product, stock);
    }

    public Page<GetProductResult> getProducts(
            String name, ProductStatus status, UUID companyId,
            String requesterRole, UUID requesterHubId,
            Pageable pageable) {

        // HUB_MANAGER는 담당 허브 소속 상품만 조회 가능 — 자동으로 hubId 필터 적용
        UUID hubFilter = ROLE_HUB_MANAGER.equals(requesterRole) ? requesterHubId : null;

        return productRepository
                .findAllWithStock(name, status, companyId, hubFilter, pageable)
                .map(GetProductResult::from);
    }

    public GetProductResult getProduct(UUID productId, String requesterRole, UUID requesterHubId) {

        Product product = productRepository.findByIdWithStock(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // HUB_MANAGER는 담당 허브 소속 상품만 조회 가능
        if (ROLE_HUB_MANAGER.equals(requesterRole) &&
                !product.getHubId().equals(requesterHubId)) {
            throw new BusinessException(ProductErrorCode.PRODUCT_NOT_ALLOWED);
        }

        return GetProductResult.from(product);
    }

    @Transactional
    public GetProductResult updateProduct(UpdateProductCommand command) {
        log.info("상품 수정 요청 - companyId: {}, requester: {}, role: {}",
                command.getCompanyId(), command.getRequesterId(), command.getRequesterRole());

        Product product = productRepository.findByIdAndDeletedAtIsNull(command.getProductId())
                .orElseThrow(() -> {
                    log.warn("상품 수정 실패 - 상품 없음, companyId: {}", command.getCompanyId());
                    return new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND);
                });

        // 상품 companyId로 company 검증
        CompanyInfo company = validateCompany(product.getCompanyId());

        // companyId 변경 차단 — MASTER만 허용
        if (!ROLE_MASTER.equals(command.getRequesterRole()) && command.getCompanyId() != null) {
            throw new BusinessException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
        }

        // hubId 수정 차단 - MASTER만 허용
        if (!ROLE_MASTER.equals(command.getRequesterRole()) && command.getHubId() != null) {
            throw new BusinessException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
        }

        // 변경하려는 hubId가 있을 시 내부 API로 실존 검증
        if (command.getHubId() != null) {
            validateHub(command.getHubId());
        }

        // 권한 검증 (내부 메서드)
        validateAuthority(
                command.getRequesterId(),
                command.getRequesterRole(),
                command.getRequesterHubId(),
                product.getHubId(),
                company);

        product.update(
                command.getName(),
                command.getPrice(),
                command.getStatus(),
                command.getCompanyId(),
                command.getHubId()
                );

        log.info("상품 정보 수정 완료 - productId: {}", command.getProductId());
        return GetProductResult.from(product);
    }

    @Transactional
    public GetProductResult updateStock(UpdateProductStockCommand command) {
        log.info("재고 수정 요청 - productId: {}, requester: {}, role: {}",
                command.getProductId(), command.getRequesterId(), command.getRequesterRole());

        // 상품 + 재고 함께 조회 (JOIN FETCH)
        Product product = productRepository.findByIdWithStock(command.getProductId())
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // 재고 수정은 MASTER, HUB_MANAGER만 가능 - COMPANY_MANAGER는 차단
        // validateAuthority는 COMPANY_MANAGER를 허용하므로 사전에 차단
        if (ROLE_COMPANY_MANAGER.equals(command.getRequesterRole())) {
            throw new BusinessException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
        }

        // 역할별 권한 검증 (HUB_MANAGER: 담당 허브 소속 상품만)
        validateAuthority(
                command.getRequesterId(),
                command.getRequesterRole(),
                command.getRequesterHubId(),
                product.getHubId(),
                null); // COMPANY_MANAGER는 차단했으므로 company 불필요

        // 재고 수정
        product.getStock().updateStock(command.getStock());

        log.info("재고 수정 완료 - productId: {}, newStock: {}", command.getProductId(), command.getStock());
        return GetProductResult.from(product);
    }

    @Transactional
    public DeleteProductResult deleteProduct(DeleteProductCommand command) {
        log.info("상품 삭제 요청 - productId: {}, requester: {}, role: {}",
                command.getProductId(), command.getRequesterId(), command.getRequesterRole());

        // 상품 + 재고 함께 조회 - 재고도 함께 연쇄적으로 삭제해야함
        Product product = productRepository.findByIdWithStock(command.getProductId())
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // 삭제는 MASTER, HUB_MANAGER만 가능
        if (ROLE_COMPANY_MANAGER.equals(command.getRequesterRole())) {
            throw new BusinessException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
        }

        // 역할별 권한 검증 (HUB_MANAGER: 담당 허브 소속 상품만)
        // company는 COMPANY_MANAGER 차단으로 인해 불필요
        validateAuthority(
                command.getRequesterId(),
                command.getRequesterRole(),
                command.getRequesterHubId(),
                product.getHubId(),
                null
        );

        // Product Soft Delete
        product.softDelete(command.getRequesterId());

        // ProductStock도 함께 Soft Delete - 상품과 재고는 생명주기가 동일
        product.getStock().softDelete(command.getRequesterId());

        log.info("상품 삭제 완료 - productId: {}", command.getProductId());
        return DeleteProductResult.from(product);
    }

    // 내부 API 아웃바운드 - 상품 단건 조회
    public GetProductResult getProductInternal(UUID productId) {
        return GetProductResult.from(
                productRepository.findByIdWithStock(productId)
                        .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND))
        );
    }

    // 내부 API 아웃바운드 - 재고 차감 (주문 생성 시 order-service 호출)
    // @Version 낙관적 락 — 동시 차감 충돌 시 OptimisticLockException 발생
    @Transactional
    public StockChangeResult decreaseStock(UUID productId, Integer quantity) {
        Product product = productRepository.findByIdWithStock(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

        int newStock = product.getStock().getStock() - quantity;

        // 재고 부족 - 0 미만 방지
        if (newStock < 0) {
            throw new BusinessException(ProductErrorCode.STOCK_NOT_ENOUGH);
        }

        product.getStock().updateStock(newStock);
        log.info("재고 차감 완료 - productId: {}, 차감: {}, 잔여: {}", productId, quantity, newStock);
        return StockChangeResult.from(product);
    }

    // 내부 API 아웃바운드 - 재고 복원 (주문 취소 시 order-service 호출)
    // order-service가 원래 수량을 그대로 전달 후 stock += quantity
    @Transactional
    public StockChangeResult restoreStock(UUID productId, Integer quantity) {
        Product product = productRepository.findByIdWithStock(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

        int newStock = product.getStock().getStock() + quantity;
        product.getStock().updateStock(newStock);
        log.info("재고 복원 완료 - productId: {}, 복원: {}, 잔여: {}", productId, quantity, newStock);
        return StockChangeResult.from(product);
    }

    // 내부 API 아웃바운드 - 업체 소속 상품 일괄 Soft Delete (업체 삭제 시 company-service 호출)
    // 결과 없어도 deletedCount: 0 / 200 OK 반환 (멱등성 보장)
    @Transactional
    public BulkDeleteResult deleteProductsByCompany(UUID companyId, UUID deletedBy) {
        List<Product> products = productRepository.findAllByCompanyIdWithStock(companyId);
        LocalDateTime deletedAt = LocalDateTime.now();

        // 각 상품과 재고를 함께 Soft Delete — 생명주기 동일
        for (Product product : products) {
            product.softDelete(deletedBy);
            product.getStock().softDelete(deletedBy);
        }

        log.info("업체 소속 상품 일괄 삭제 완료 - companyId: {}, 삭제 수: {}", companyId, products.size());
        return BulkDeleteResult.builder()
                .companyId(companyId)
                .deletedCount(products.size())
                .deletedAt(deletedAt)
                .build();
    }

    // 내부 API 인바운드 - 업체 존재 여부 검증 (By. company-service)
    private CompanyInfo validateCompany(UUID companyId) {
        CompanyResponse companyResponse = companyClient.getCompany(companyId, INTERNAL_REQUEST).getData();
        if (companyResponse == null) {
            throw new BusinessException(ProductErrorCode.COMPANY_NOT_FOUND);
        }
        return CompanyInfo.from(companyResponse); // Infrastructure DTO → Application 변환
    }

    // 내부 API 인바운드 - 허브 존재 여부 검증 (By. hub-service)
    private void validateHub (UUID hubId) {
        HubResponse hub = hubClient.getHub(hubId, INTERNAL_REQUEST).getData();
        if (hub == null) {
            throw new BusinessException(ProductErrorCode.HUB_NOT_FOUND);
        }
    }

    // 역할별 권한 검증
    private void validateAuthority(
            UUID requesterId,
            String requesterRole,
            UUID requesterHubId,
            UUID targetHubId,
            CompanyInfo company
    ) {
        if (ROLE_MASTER.equals(requesterRole)) {
            // MASTER : 제한 없음
            return;
        }

        if (ROLE_HUB_MANAGER.equals(requesterRole)) {
            // HUB_MANAGER: 담당 허브 소속 상품만 조작 가능
            if (!requesterHubId.equals(targetHubId)) {
                throw new BusinessException(ProductErrorCode.PRODUCT_NOT_ALLOWED);
            }
            return;
        }

        if (ROLE_COMPANY_MANAGER.equals(requesterRole)) {
            // COMPANY_MANAGER: 본인 업체 상품만 조작 가능
            if (!requesterId.equals(company.manager())) {
                throw new BusinessException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
            }
            return;
        }

        // 위 역할 외에는 모두 거부
        throw new BusinessException(ProductErrorCode.PRODUCT_ACCESS_DENIED);
    }


}
