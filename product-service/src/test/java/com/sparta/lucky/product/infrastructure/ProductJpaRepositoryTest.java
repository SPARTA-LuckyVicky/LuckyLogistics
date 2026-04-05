package com.sparta.lucky.product.infrastructure;

import com.sparta.lucky.product.domain.Product;
import com.sparta.lucky.product.domain.ProductStatus;
import com.sparta.lucky.product.domain.ProductStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ProductJpaRepository 통합 테스트
 * 검증 대상
 * - findAllWithStock  : 동적 조건 쿼리 + @EntityGraph stock 로딩
 * - findByIdWithStock : JOIN FETCH 단건 조회 + soft delete 필터
 * - findAllByCompanyIdWithStock : 업체 소속 일괄 조회 + soft delete 필터
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductJpaRepositoryTest {

    // AuditConfig 빈을 @Primary로 덮어써 X-User-Id 헤더 없이도 Auditing이 동작하도록 설정
    @TestConfiguration
    @EnableJpaAuditing
    static class TestAuditConfig {
        @Bean
        @Primary
        public AuditorAware<UUID> auditorAware() {
            return () -> Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        }
    }

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private ProductStockJpaRepository productStockJpaRepository;

    // 조회 전 캐시 1차캐시 비우는 용도
    @Autowired
    TestEntityManager testEntityManager;

    // 테스트 고정 UUID
    private static final UUID COMPANY_A = UUID.randomUUID();
    private static final UUID COMPANY_B = UUID.randomUUID();
    private static final UUID HUB_A = UUID.randomUUID();
    private static final UUID HUB_B = UUID.randomUUID();
    private static final Pageable PAGE = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        saveProductWithStock("서울건조식품", ProductStatus.ACTIVE, COMPANY_A, HUB_A, 100);
        saveProductWithStock("부산냉동물류", ProductStatus.INACTIVE, COMPANY_A, HUB_A, 50);
        saveProductWithStock("서울전자부품", ProductStatus.ACTIVE, COMPANY_A, HUB_B, 30);
        saveProductWithStock("대구신선식품", ProductStatus.ACTIVE, COMPANY_B, HUB_B, 20);
    }

    @Test
    @DisplayName("조건 없으면 Soft Delete 되지 않은 상품 전체 조회")
    void findAllWithStock_noCondition() {
        Page<Product> result = productJpaRepository.findAllWithStock(null, null, null, null, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(4);
    }

    @Test
    @DisplayName("name 부분 검색 - '서울' 포함 상품 2건")
    void findAllWithStock_byName() {
        Page<Product> result = productJpaRepository.findAllWithStock("서울", null, null, null, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(p -> p.getName().contains("서울"));
    }

    @Test
    @DisplayName("status 필터 - INACTIVE 1건")
    void findAllWithStock_byStatus() {
        Page<Product> result = productJpaRepository.findAllWithStock(null, ProductStatus.INACTIVE, null, null, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).allMatch(p -> p.getStatus() == ProductStatus.INACTIVE);
    }

    @Test
    @DisplayName("companyId 필터 - COMPANY_A 소속 3건")
    void findAllWithStock_byCompanyId() {
        Page<Product> result = productJpaRepository.findAllWithStock(null, null, COMPANY_A, null, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent()).allMatch(p -> p.getCompanyId().equals(COMPANY_A));
    }

    @Test
    @DisplayName("hubId 필터 - HUB_A 소속 2건")
    void findAllWithStock_byHubId() {
        Page<Product> result = productJpaRepository.findAllWithStock(null, null, null, HUB_A, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(p -> p.getHubId().equals(HUB_A));
    }

    @Test
    @DisplayName("name + hubId 복합 조건 - '서울' + HUB_A = 서울건조식품 1건")
    void findAllWithStock_byNameAndHubId() {
        Page<Product> result = productJpaRepository.findAllWithStock("서울", null, null, HUB_A, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("서울건조식품");
    }

    @Test
    @DisplayName("Soft Delete된 상품은 목록 조회에서 제외")
    void findAllWithStock_excludesSoftDeleted() {
        // 상품 1건 Soft Delete
        Product toDelete = productJpaRepository.findAll().get(0);
        toDelete.softDelete(UUID.randomUUID());
        productJpaRepository.save(toDelete);

        Page<Product> result = productJpaRepository.findAllWithStock(null, null, null, null, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("id로 상품 단건 조회 — stock JOIN FETCH로 함께 로딩")
    void findByIdWithStock_found() {
        UUID targetId = productJpaRepository.findAll().get(0).getId();

        // findAll()이 stock을 LAZY로 남긴 채 캐시에 올렸으므로
        // 1차 캐시를 초기화해야 findByIdWithStock의 JOIN FETCH 결과가 반영됨
        testEntityManager.flush();
        testEntityManager.getEntityManager().clear();

        Optional<Product> result = productJpaRepository.findByIdWithStock(targetId);

        assertThat(result).isPresent();
        assertThat(result.get().getStock()).isNotNull();
        assertThat(result.get().getStock().getStock()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 id → Optional.empty()")
    void findByIdWithStock_notFound() {
        Optional<Product> result = productJpaRepository.findByIdWithStock(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Soft Delete된 상품은 단건 조회에서 제외")
    void findByIdWithStock_excludesSoftDeleted() {
        Product product = productJpaRepository.findAll().get(0);
        product.softDelete(UUID.randomUUID());
        productJpaRepository.save(product);

        Optional<Product> result = productJpaRepository.findByIdWithStock(product.getId());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("업체 소속 상품 일괄 조회 - COMPANY_A 소속 3건")
    void findAllByCompanyIdWithStock_found() {
        List<Product> result = productJpaRepository.findAllByCompanyIdWithStock(COMPANY_A);
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(p -> p.getCompanyId().equals(COMPANY_A));
    }

    @Test
    @DisplayName("Soft Delete된 상품은 일괄 조회에서 제외")
    void findAllByCompanyIdWithStock_excludesSoftDeleted() {
        // COMPANY_A 소속 3건 중 1건 Soft Delete
        productJpaRepository.findAllByCompanyIdWithStock(COMPANY_A)
                .get(0)
                .softDelete(UUID.randomUUID());

        List<Product> result = productJpaRepository.findAllByCompanyIdWithStock(COMPANY_A);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("소속 상품 없는 업체 조회 → 빈 리스트 (멱등성)")
    void findAllByCompanyIdWithStock_emptyCompany() {
        List<Product> result = productJpaRepository.findAllByCompanyIdWithStock(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    /**
     * Product → ProductStock 순서로 저장.
     * ProductStock이 연관관계 주인(@JoinColumn)이므로
     * Product를 먼저 persist해 ID를 확보한 뒤 Stock을 저장해야 한다.
     */
    private void saveProductWithStock(String name, ProductStatus status,
                                      UUID companyId, UUID hubId, int stockAmount) {
        Product product = Product.builder()
                .companyId(companyId).hubId(hubId)
                .name(name).price(1_000).status(status)
                .build();
        productJpaRepository.save(product);

        ProductStock stock = ProductStock.builder()
                .product(product).hubId(hubId).stock(stockAmount)
                .build();
        productStockJpaRepository.save(stock);
    }
}