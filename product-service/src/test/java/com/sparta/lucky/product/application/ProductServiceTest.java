package com.sparta.lucky.product.application;

import com.sparta.lucky.product.application.dto.*;
import com.sparta.lucky.product.common.exception.BusinessException;
import com.sparta.lucky.product.domain.*;
import com.sparta.lucky.product.infrastructure.feign.CompanyClient;
import com.sparta.lucky.product.infrastructure.feign.HubClient;
import com.sparta.lucky.product.infrastructure.feign.dto.CompanyResponse;
import com.sparta.lucky.product.infrastructure.feign.dto.FeignApiResponse;
import com.sparta.lucky.product.infrastructure.feign.dto.HubResponse;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * ProductService 단위 테스트
 *
 * 테스트 제외 사항
 * - getProductInternal : findByIdWithStock 결과를 그대로 DTO로 변환만 함 — Repository 계층에서 검증
 * - Page/Sort 인코딩 : Controller/JPA 계층의 검증 범위
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductStockRepository productStockRepository;

    @Mock
    private CompanyClient companyClient;

    @Mock
    private HubClient hubClient;

    // 테스트용 UUID
    private static final UUID PRODUCT_ID   = UUID.randomUUID();
    private static final UUID COMPANY_ID   = UUID.randomUUID();
    private static final UUID HUB_A        = UUID.randomUUID();
    private static final UUID HUB_B        = UUID.randomUUID();
    private static final UUID REQUESTER_ID = UUID.randomUUID();
    private static final UUID MANAGER_ID   = UUID.randomUUID(); // COMPANY_MANAGER의 업체 manager 값

    private Product buildProduct(int stockAmount) {
        ProductStock stock = ProductStock.builder()
                .hubId(HUB_A)
                .stock(stockAmount)
                .build();
        return Product.builder()
                .id(PRODUCT_ID)
                .companyId(COMPANY_ID)
                .hubId(HUB_A)
                .name("테스트상품")
                .price(10_000)
                .status(ProductStatus.ACTIVE)
                .stock(stock)
                .build();
    }

    /**
     * CompanyClient 성공 스텁
     * CompanyResponse는 @Getter @NoArgsConstructor 전용이라 builder/setter가 없음
     * Mockito mock으로 생성한 뒤 getter를 스텁
     */
    @SuppressWarnings("unchecked")
    private void stubCompanySuccess(UUID companyId, UUID hubId, UUID managerId) {
        CompanyResponse companyResp = mock(CompanyResponse.class);
        given(companyResp.getId()).willReturn(companyId);
        given(companyResp.getHubId()).willReturn(hubId);
        given(companyResp.getManager()).willReturn(managerId);

        FeignApiResponse<CompanyResponse> feignResp = mock(FeignApiResponse.class);
        given(companyClient.getCompany(eq(companyId), any())).willReturn(feignResp);
        // requireData: success=true, data=companyResp 로 동작
        given(feignResp.requireData(any())).willReturn(companyResp);
    }

    /**
     * HubClient 성공 스텁
     */
    @SuppressWarnings("unchecked")
    private void stubHubSuccess(UUID hubId) {
        HubResponse hubResp = mock(HubResponse.class);
        FeignApiResponse<HubResponse> feignResp = mock(FeignApiResponse.class);
        given(hubClient.getHub(eq(hubId), any())).willReturn(feignResp);
        given(feignResp.requireData(any())).willReturn(hubResp);
    }

    @Nested
    @DisplayName("재고 차감 (내부 API)")
    class DecreaseStock {

        @Test
        @DisplayName("수량이 null 이면 INVALID_STOCK_QUANTITY")
        void nullQuantity_throwsInvalidQuantity() {
            assertThatThrownBy(() -> productService.decreaseStock(PRODUCT_ID, null))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.INVALID_STOCK_QUANTITY.getCode()));
        }

        @Test
        @DisplayName("수량이 0 이면 INVALID_STOCK_QUANTITY")
        void zeroQuantity_throwsInvalidQuantity() {
            // quantity <= 0 조건 — 0은 실제 재고를 바꾸지 않으므로 명시적으로 막아야 한다
            assertThatThrownBy(() -> productService.decreaseStock(PRODUCT_ID, 0))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.INVALID_STOCK_QUANTITY.getCode()));
        }

        @Test
        @DisplayName("상품 없으면 PRODUCT_NOT_FOUND")
        void productNotFound_throwsNotFound() {
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productService.decreaseStock(PRODUCT_ID, 5))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("차감 후 재고가 0 미만이면 STOCK_NOT_ENOUGH")
        void insufficientStock_throwsStockNotEnough() {
            // 현재 재고 3, 차감 5 → newStock = -2 < 0
            Product product = buildProduct(3);
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            assertThatThrownBy(() -> productService.decreaseStock(PRODUCT_ID, 5))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.STOCK_NOT_ENOUGH.getCode()));
        }

        @Test
        @DisplayName("정상 차감 - 도메인 상태와 반환값 모두 검증")
        void success_stockDecremented() {
            // 현재 재고 10, 차감 3 → 잔여 7
            Product product = buildProduct(10);
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            StockChangeResult result = productService.decreaseStock(PRODUCT_ID, 3);

            // 반환값 검증
            assertThat(result.getStock()).isEqualTo(7);
            // 도메인 상태 검증 (updateStock 실제 호출 여부 확인)
            assertThat(product.getStock().getStock()).isEqualTo(7);
        }
    }

    @Nested
    @DisplayName("재고 복원 (내부 API)")
    class RestoreStock {

        @Test
        @DisplayName("수량이 0 이하이면 INVALID_STOCK_QUANTITY")
        void invalidQuantity_throwsInvalidQuantity() {
            assertThatThrownBy(() -> productService.restoreStock(PRODUCT_ID, 0))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.INVALID_STOCK_QUANTITY.getCode()));
        }

        @Test
        @DisplayName("상품 없으면 PRODUCT_NOT_FOUND")
        void productNotFound_throwsNotFound() {
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productService.restoreStock(PRODUCT_ID, 5))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("int 오버플로우 방지 - currentStock > MAX_VALUE - quantity 시 STOCK_OVERFLOW")
        void overflow_throwsStockOverflow() {
            // MAX_VALUE - 1 + 2 = MAX_VALUE + 1 → wrap-around 위험 → 차단
            Product product = buildProduct(Integer.MAX_VALUE - 1);
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            assertThatThrownBy(() -> productService.restoreStock(PRODUCT_ID, 2))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.STOCK_OVERFLOW.getCode()));
        }

        @Test
        @DisplayName("정상 복원 - 도메인 상태와 반환값 모두 검증")
        void success_stockRestored() {
            // 현재 재고 7, 복원 3 → 잔여 10
            Product product = buildProduct(7);
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            StockChangeResult result = productService.restoreStock(PRODUCT_ID, 3);

            assertThat(result.getStock()).isEqualTo(10);
            assertThat(product.getStock().getStock()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("업체 소속 상품 일괄 삭제 (내부 API)")
    class DeleteProductsByCompany {

        @Test
        @DisplayName("소속 상품 없으면 deletedCount = 0 반환 (멱등성)")
        void noProducts_returnsZeroCount() {
            given(productRepository.findAllByCompanyIdWithStock(COMPANY_ID)).willReturn(List.of());

            BulkDeleteResult result = productService.deleteProductsByCompany(COMPANY_ID, REQUESTER_ID);

            assertThat(result.getDeletedCount()).isEqualTo(0);
            assertThat(result.getCompanyId()).isEqualTo(COMPANY_ID);
        }

        @Test
        @DisplayName("소속 상품 존재 → 상품 + 재고 모두 Soft Delete 후 count 반환")
        void withProducts_allSoftDeleted() {
            Product product1 = buildProduct(5);
            Product product2 = buildProduct(10);
            given(productRepository.findAllByCompanyIdWithStock(COMPANY_ID))
                    .willReturn(List.of(product1, product2));

            BulkDeleteResult result = productService.deleteProductsByCompany(COMPANY_ID, REQUESTER_ID);

            assertThat(result.getDeletedCount()).isEqualTo(2);
            // 도메인 상태 검증: softDelete 호출 시 deletedAt이 채워진다
            assertThat(product1.getDeletedAt()).isNotNull();
            assertThat(product2.getDeletedAt()).isNotNull();
            assertThat(product1.getStock().getDeletedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("상품 생성")
    class CreateProduct {

        // createProduct 흐름: validateCompany → validateHub → validateAuthority → save

        @Test
        @DisplayName("MASTER는 어느 허브 / 업체에나 상품 생성 가능")
        void master_canCreate() {
            stubCompanySuccess(COMPANY_ID, HUB_A, MANAGER_ID);
            stubHubSuccess(HUB_A);
            // save 호출 시 인자를 그대로 반환 (Id 생성 없이 동작 확인용)
            given(productRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
            given(productStockRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            CreateProductCommand command = CreateProductCommand.builder()
                    .companyId(COMPANY_ID).hubId(HUB_A)
                    .name("상품A").price(1_000).stock(50)
                    .requesterId(REQUESTER_ID).requesterRole("MASTER")
                    .requesterHubId(null)
                    .build();

            assertThatCode(() -> productService.createProduct(command))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("HUB_MANAGER는 담당 허브 업체 상품만 생성 가능")
        void hubManager_ownHub_canCreate() {
            // company.hubId = HUB_A, requesterHubId = HUB_A → 일치
            stubCompanySuccess(COMPANY_ID, HUB_A, MANAGER_ID);
            stubHubSuccess(HUB_A);
            given(productRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
            given(productStockRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            CreateProductCommand command = CreateProductCommand.builder()
                    .companyId(COMPANY_ID).hubId(HUB_A)
                    .name("상품A").price(1_000).stock(50)
                    .requesterId(REQUESTER_ID).requesterRole("HUB_MANAGER")
                    .requesterHubId(HUB_A)
                    .build();

            assertThatCode(() -> productService.createProduct(command))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("HUB_MANAGER가 다른 허브 상품 생성 시 PRODUCT_NOT_ALLOWED")
        void hubManager_otherHub_throwsNotAllowed() {
            // validateAuthority: requesterHubId(HUB_B) ≠ targetHubId(HUB_A) → 예외
            stubCompanySuccess(COMPANY_ID, HUB_A, MANAGER_ID);
            stubHubSuccess(HUB_A);

            CreateProductCommand command = CreateProductCommand.builder()
                    .companyId(COMPANY_ID).hubId(HUB_A)
                    .name("상품A").price(1_000).stock(50)
                    .requesterId(REQUESTER_ID).requesterRole("HUB_MANAGER")
                    .requesterHubId(HUB_B) // 담당 허브 ≠ 상품 허브
                    .build();

            assertThatThrownBy(() -> productService.createProduct(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_NOT_ALLOWED.getCode()));
        }

        @Test
        @DisplayName("COMPANY_MANAGER가 본인 업체(company.manager == requesterId) 상품 생성 가능")
        void companyManager_isManager_canCreate() {
            // company.manager = MANAGER_ID, requesterId = MANAGER_ID → 일치 → 통과
            stubCompanySuccess(COMPANY_ID, HUB_A, MANAGER_ID);
            stubHubSuccess(HUB_A);
            given(productRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
            given(productStockRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            CreateProductCommand command = CreateProductCommand.builder()
                    .companyId(COMPANY_ID).hubId(HUB_A)
                    .name("상품A").price(1_000).stock(50)
                    .requesterId(MANAGER_ID) // company.manager와 동일
                    .requesterRole("COMPANY_MANAGER")
                    .requesterHubId(null)
                    .build();

            assertThatCode(() -> productService.createProduct(command))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("COMPANY_MANAGER가 타 업체 상품 생성 시 PRODUCT_ACCESS_DENIED")
        void companyManager_notManager_throwsAccessDenied() {
            // company.manager = MANAGER_ID, requesterId = REQUESTER_ID → 불일치
            stubCompanySuccess(COMPANY_ID, HUB_A, MANAGER_ID);
            stubHubSuccess(HUB_A);

            CreateProductCommand command = CreateProductCommand.builder()
                    .companyId(COMPANY_ID).hubId(HUB_A)
                    .name("상품A").price(1_000).stock(50)
                    .requesterId(REQUESTER_ID) // MANAGER_ID와 다름
                    .requesterRole("COMPANY_MANAGER")
                    .requesterHubId(null)
                    .build();

            assertThatThrownBy(() -> productService.createProduct(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("company-service가 404 반환 시 BusinessException(COMPANY_NOT_FOUND)")
        void companyFeign404_throwsCompanyNotFound() {
            // validateCompany() 내부에서 FeignException.NotFound를 잡아 BusinessException으로 변환
            FeignException.NotFound notFound = mock(FeignException.NotFound.class);
            given(companyClient.getCompany(any(), any())).willThrow(notFound);

            CreateProductCommand command = CreateProductCommand.builder()
                    .companyId(COMPANY_ID).hubId(HUB_A)
                    .name("상품A").price(1_000).stock(50)
                    .requesterId(REQUESTER_ID).requesterRole("MASTER")
                    .requesterHubId(null)
                    .build();

            assertThatThrownBy(() -> productService.createProduct(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.COMPANY_NOT_FOUND.getCode()));
        }
    }

    @Nested
    @DisplayName("상품 목록 조회")
    class GetProducts {

        @Test
        @DisplayName("HUB_MANAGER가 X-Hub-Id 없이 조회 시 PRODUCT_NOT_ALLOWED")
        void hubManager_nullHubId_throwsNotAllowed() {
            // requesterHubId == null → 전체 조회로 열리는 버그 방지용 조기 차단
            assertThatThrownBy(() ->
                    productService.getProducts(null, null, null, "HUB_MANAGER", null, PageRequest.of(0, 10)))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_NOT_ALLOWED.getCode()));
        }

        @Test
        @DisplayName("HUB_MANAGER 조회 시 담당 허브(requesterHubId)가 hubId 필터로 자동 적용")
        void hubManager_hubFilterApplied() {
            given(productRepository.findAllWithStock(any(), any(), any(), any(), any()))
                    .willReturn(Page.empty());

            productService.getProducts(null, null, null, "HUB_MANAGER", HUB_A, PageRequest.of(0, 10));

            // findAllWithStock에 HUB_A가 hubId 파라미터로 전달됐는지 캡처해서 검증
            ArgumentCaptor<UUID> hubIdCaptor = ArgumentCaptor.forClass(UUID.class);
            verify(productRepository).findAllWithStock(any(), any(), any(), hubIdCaptor.capture(), any());
            assertThat(hubIdCaptor.getValue()).isEqualTo(HUB_A);
        }

        @Test
        @DisplayName("MASTER 조회 시 hubId 필터 없이(null) 전체 조회")
        void master_noHubFilter() {
            given(productRepository.findAllWithStock(any(), any(), any(), any(), any()))
                    .willReturn(Page.empty());

            productService.getProducts(null, null, null, "MASTER", null, PageRequest.of(0, 10));

            ArgumentCaptor<UUID> hubIdCaptor = ArgumentCaptor.forClass(UUID.class);
            verify(productRepository).findAllWithStock(any(), any(), any(), hubIdCaptor.capture(), any());
            assertThat(hubIdCaptor.getValue()).isNull();
        }
    }

    @Nested
    @DisplayName("상품 단건 조회")
    class GetProduct {

        @Test
        @DisplayName("존재하지 않는 상품 → PRODUCT_NOT_FOUND")
        void notFound_throwsProductNotFound() {
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() ->
                    productService.getProduct(PRODUCT_ID, "MASTER", null))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("HUB_MANAGER가 담당 허브 상품 단건 조회 성공")
        void hubManager_ownHub_success() {
            Product product = buildProduct(10); // product.hubId = HUB_A
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            assertThatCode(() ->
                    productService.getProduct(PRODUCT_ID, "HUB_MANAGER", HUB_A))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("HUB_MANAGER가 다른 허브 상품 조회 시 PRODUCT_NOT_ALLOWED")
        void hubManager_otherHub_throwsNotAllowed() {
            Product product = buildProduct(10); // product.hubId = HUB_A
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            assertThatThrownBy(() ->
                    productService.getProduct(PRODUCT_ID, "HUB_MANAGER", HUB_B))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_NOT_ALLOWED.getCode()));
        }
    }

    @Nested
    @DisplayName("상품 수정")
    class UpdateProduct {

        @Test
        @DisplayName("MASTER 외 역할이 companyId 변경 시도 → PRODUCT_ACCESS_DENIED")
        void nonMaster_changeCompanyId_throwsAccessDenied() {
            Product product = buildProduct(10);
            given(productRepository.findByIdAndDeletedAtIsNull(PRODUCT_ID)).willReturn(Optional.of(product));
            // validateCompany(product.companyId) 호출용 스텁
            stubCompanySuccess(COMPANY_ID, HUB_A, MANAGER_ID);

            UpdateProductCommand command = UpdateProductCommand.builder()
                    .productId(PRODUCT_ID)
                    .companyId(UUID.randomUUID()) // companyId 변경 시도
                    .requesterId(REQUESTER_ID).requesterRole("HUB_MANAGER")
                    .requesterHubId(HUB_A)
                    .build();

            assertThatThrownBy(() -> productService.updateProduct(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("MASTER 외 역할이 hubId 변경 시도 → PRODUCT_ACCESS_DENIED")
        void nonMaster_changeHubId_throwsAccessDenied() {
            Product product = buildProduct(10);
            given(productRepository.findByIdAndDeletedAtIsNull(PRODUCT_ID)).willReturn(Optional.of(product));
            stubCompanySuccess(COMPANY_ID, HUB_A, MANAGER_ID);

            UpdateProductCommand command = UpdateProductCommand.builder()
                    .productId(PRODUCT_ID)
                    .hubId(HUB_B) // hubId 변경 시도
                    .requesterId(REQUESTER_ID).requesterRole("COMPANY_MANAGER")
                    .requesterHubId(null)
                    .build();

            assertThatThrownBy(() -> productService.updateProduct(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("MASTER는 이름/가격 수정 가능 - 도메인 상태 반영 검증")
        void master_canUpdateBasicFields() {
            Product product = buildProduct(10);
            given(productRepository.findByIdAndDeletedAtIsNull(PRODUCT_ID)).willReturn(Optional.of(product));
            stubCompanySuccess(COMPANY_ID, HUB_A, MANAGER_ID);

            UpdateProductCommand command = UpdateProductCommand.builder()
                    .productId(PRODUCT_ID)
                    .name("수정된상품명").price(2_000)
                    // companyId, hubId 미전달 → 변경 없음
                    .requesterId(REQUESTER_ID).requesterRole("MASTER")
                    .requesterHubId(null)
                    .build();

            assertThatCode(() -> productService.updateProduct(command)).doesNotThrowAnyException();
            assertThat(product.getName()).isEqualTo("수정된상품명");
            assertThat(product.getPrice()).isEqualTo(2_000);
        }
    }

    @Nested
    @DisplayName("재고 수정 (외부 API)")
    class UpdateStock {

        @Test
        @DisplayName("COMPANY_MANAGER는 재고 수정 불가 → PRODUCT_ACCESS_DENIED")
        void companyManager_throwsAccessDenied() {
            Product product = buildProduct(10);
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            UpdateProductStockCommand command = UpdateProductStockCommand.builder()
                    .productId(PRODUCT_ID).stock(20)
                    .requesterId(REQUESTER_ID).requesterRole("COMPANY_MANAGER")
                    .requesterHubId(null)
                    .build();

            assertThatThrownBy(() -> productService.updateStock(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("HUB_MANAGER가 담당 허브 상품 재고 수정 가능 - 도메인 상태 검증")
        void hubManager_ownHub_success() {
            Product product = buildProduct(10);
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            UpdateProductStockCommand command = UpdateProductStockCommand.builder()
                    .productId(PRODUCT_ID).stock(30)
                    .requesterId(REQUESTER_ID).requesterRole("HUB_MANAGER")
                    .requesterHubId(HUB_A) // 담당 허브 = 상품 허브(HUB_A)
                    .build();

            assertThatCode(() -> productService.updateStock(command)).doesNotThrowAnyException();
            assertThat(product.getStock().getStock()).isEqualTo(30);
        }

        @Test
        @DisplayName("HUB_MANAGER가 다른 허브 상품 재고 수정 시 PRODUCT_NOT_ALLOWED")
        void hubManager_otherHub_throwsNotAllowed() {
            Product product = buildProduct(10);
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            UpdateProductStockCommand command = UpdateProductStockCommand.builder()
                    .productId(PRODUCT_ID).stock(30)
                    .requesterId(REQUESTER_ID).requesterRole("HUB_MANAGER")
                    .requesterHubId(HUB_B) // 담당 허브 ≠ 상품 허브
                    .build();

            assertThatThrownBy(() -> productService.updateStock(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_NOT_ALLOWED.getCode()));
        }
    }

    @Nested
    @DisplayName("상품 삭제")
    class DeleteProduct {

        @Test
        @DisplayName("존재하지 않는 상품 삭제 시 PRODUCT_NOT_FOUND")
        void notFound_throwsProductNotFound() {
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.empty());

            DeleteProductCommand command = DeleteProductCommand.builder()
                    .productId(PRODUCT_ID)
                    .requesterId(REQUESTER_ID).requesterRole("MASTER")
                    .requesterHubId(null)
                    .build();

            assertThatThrownBy(() -> productService.deleteProduct(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND.getCode()));
        }

        @Test
        @DisplayName("COMPANY_MANAGER는 삭제 불가 → PRODUCT_ACCESS_DENIED")
        void companyManager_throwsAccessDenied() {
            Product product = buildProduct(10);
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            DeleteProductCommand command = DeleteProductCommand.builder()
                    .productId(PRODUCT_ID)
                    .requesterId(REQUESTER_ID).requesterRole("COMPANY_MANAGER")
                    .requesterHubId(null)
                    .build();

            assertThatThrownBy(() -> productService.deleteProduct(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("HUB_MANAGER가 담당 허브 상품 삭제 가능 - 상품 + 재고 Soft Delete 검증")
        void hubManager_ownHub_success() {
            Product product = buildProduct(10);
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            DeleteProductCommand command = DeleteProductCommand.builder()
                    .productId(PRODUCT_ID)
                    .requesterId(REQUESTER_ID).requesterRole("HUB_MANAGER")
                    .requesterHubId(HUB_A)
                    .build();

            assertThatCode(() -> productService.deleteProduct(command)).doesNotThrowAnyException();
            assertThat(product.getDeletedAt()).isNotNull(); // 상품 Soft Delete 확인
            assertThat(product.getStock().getDeletedAt()).isNotNull(); // 재고 Soft Delete 확인
        }

        @Test
        @DisplayName("HUB_MANAGER가 다른 허브 상품 삭제 시 PRODUCT_NOT_ALLOWED")
        void hubManager_otherHub_throwsNotAllowed() {
            Product product = buildProduct(10);
            given(productRepository.findByIdWithStock(PRODUCT_ID)).willReturn(Optional.of(product));

            DeleteProductCommand command = DeleteProductCommand.builder()
                    .productId(PRODUCT_ID)
                    .requesterId(REQUESTER_ID).requesterRole("HUB_MANAGER")
                    .requesterHubId(HUB_B)
                    .build();

            assertThatThrownBy(() -> productService.deleteProduct(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ProductErrorCode.PRODUCT_NOT_ALLOWED.getCode()));
        }
    }
}