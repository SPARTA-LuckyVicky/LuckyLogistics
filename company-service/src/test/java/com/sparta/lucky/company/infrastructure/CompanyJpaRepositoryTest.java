package com.sparta.lucky.company.infrastructure;

import com.sparta.lucky.company.common.config.AuditConfig;
import com.sparta.lucky.company.domain.Company;
import com.sparta.lucky.company.domain.CompanyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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

@DataJpaTest
@Import(AuditConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 DB 사용
class CompanyJpaRepositoryTest {

    // 테스트용 AuditorAware - AuditConfig의 빈을 덮어씀
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
    private CompanyJpaRepository companyJpaRepository;

    private static final UUID HUB_A = UUID.randomUUID();
    private static final UUID HUB_B = UUID.randomUUID();
    private static final Pageable PAGE = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        // 각 테스트마다 동일한 초기 데이터 4개 삽입
        companyJpaRepository.saveAll(List.of(
                Company.builder().name("서울건조식품").companyType(CompanyType.SUPPLIER).hubId(HUB_A).address("주소1").build(),
                Company.builder().name("부산냉동물류").companyType(CompanyType.RECEIVER).hubId(HUB_A).address("주소2").build(),
                Company.builder().name("서울전자부품").companyType(CompanyType.SUPPLIER).hubId(HUB_B).address("주소3").build(),
                Company.builder().name("대구신선식품").companyType(CompanyType.RECEIVER).hubId(HUB_B).address("주소4").build()
        ));
    }

    @Test
    @DisplayName("조건 없으면 전체 조회")
    void findAll_noCondition() {
        Page<Company> result = companyJpaRepository.findAllByConditions(null, null, null, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(4);
    }

    @Test
    @DisplayName("name 부분 검색")
    void findAll_byName() {
        Page<Company> result = companyJpaRepository.findAllByConditions("서울", null, null, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(c -> c.getName().contains("서울"));
    }

    @Test
    @DisplayName("type 필터")
    void findAll_byType() {
        Page<Company> result = companyJpaRepository.findAllByConditions(null, CompanyType.SUPPLIER, null, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(c -> c.getCompanyType() == CompanyType.SUPPLIER);
    }

    @Test
    @DisplayName("hubId 필터")
    void findAll_byHubId() {
        Page<Company> result = companyJpaRepository.findAllByConditions(null, null, HUB_A, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(c -> c.getHubId().equals(HUB_A));
    }

    @Test
    @DisplayName("name + type 복합 조건")
    void findAll_byNameAndType() {
        Page<Company> result = companyJpaRepository.findAllByConditions("서울", CompanyType.SUPPLIER, null, PAGE);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .allMatch(c -> c.getName().contains("서울") && c.getCompanyType() == CompanyType.SUPPLIER);
    }

    @Test
    @DisplayName("Soft Delete된 업체는 조회 제외")
    void findAll_excludesSoftDeleted() {
        // given: 업체 하나 soft delete
        Company toDelete = companyJpaRepository.findAll().get(0);
        toDelete.softDelete(UUID.randomUUID());
        companyJpaRepository.save(toDelete);

        // when
        Page<Company> result = companyJpaRepository.findAllByConditions(null, null, null, PAGE);

        // then: 4개 중 1개 삭제 → 3개만 조회
        assertThat(result.getTotalElements()).isEqualTo(3);
    }
}