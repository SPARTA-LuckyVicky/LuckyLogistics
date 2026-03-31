package com.sparta.lucky.company.application;

import com.sparta.lucky.company.application.dto.CreateCompanyCommand;
import com.sparta.lucky.company.application.dto.CreateCompanyResult;
import com.sparta.lucky.company.application.dto.UpdateCompanyCommand;
import com.sparta.lucky.company.common.exception.BusinessException;
import com.sparta.lucky.company.domain.Company;
import com.sparta.lucky.company.domain.CompanyErrorCode;
import com.sparta.lucky.company.domain.CompanyRepository;
import com.sparta.lucky.company.domain.CompanyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * 2026-03-31 박동진
 * CRUD중 업체 생성, 업체 수정, 업체 삭제 테스트 코드
 * 업체 단건 조회 - 프레임워크가 검증하므로 패스
 * 업체 목록 조회 - 서비스레이어에서는 Repository에 위임만 하는 형태로, @DataJpaTest에서 진행해야함
 *
 */

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    // 테스트용 고정 UUID
    private static final UUID COMPANY_ID = UUID.randomUUID();
    private static final UUID HUB_A = UUID.randomUUID();
    private static final UUID HUB_B = UUID.randomUUID();
    private static final UUID REQUESTER_ID = UUID.randomUUID();

    // 테스트마다 재사용할 기본 업체 엔티티
    private Company company;

    @BeforeEach
    void setUp() {
        company = Company.builder()
                .id(COMPANY_ID)
                .name("테스트업체")
                .companyType(CompanyType.SUPPLIER)
                .hubId(HUB_A)
                .address("서울시 강남구 1번길")
                .build();
    }

    // createCompany
    @Nested
    @DisplayName("업체 생성")
    class CreateCompany {

        @Test
        @DisplayName("MASTER는 어떤 허브에도 업체 생성 가능")
        void master_canCreateInAnyHub() {
            // given
            CreateCompanyCommand command = CreateCompanyCommand.builder()
                    .name("테스트업체")
                    .companyType(CompanyType.SUPPLIER)
                    .hubId(HUB_A)
                    .address("서울시 종로구 가1길")
                    .requesterId(REQUESTER_ID)
                    .requesterRole("MASTER")
                    .requesterHubId(null)
                    .build();
            given(companyRepository.save(any())).willReturn(company);

            // when
            CreateCompanyResult result = companyService.createCompany(command);

            // then — 반환값 검증
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("테스트업체");
        }

        @Test
        @DisplayName("HUB_MANAGER가 자신의 허브에 업체 생성 성공")
        void hubManager_canCreateInOwnHub() {
            // given
            CreateCompanyCommand command = CreateCompanyCommand.builder()
                    .name("테스트업체").companyType(CompanyType.SUPPLIER)
                    .hubId(HUB_A).address("주소")
                    .requesterId(REQUESTER_ID).requesterRole("HUB_MANAGER")
                    .requesterHubId(HUB_A)
                    .build();
            given(companyRepository.save(any())).willReturn(company);

            // when
            CreateCompanyResult result = companyService.createCompany(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getHubId()).isEqualTo(HUB_A);
        }

        @Test
        @DisplayName("HUB_MANAGER가 다른 허브에 업체 생성 시 예외")
        void hubManager_cannotCreateInOtherHub() {
            // given
            CreateCompanyCommand command = CreateCompanyCommand.builder()
                    .name("테스트업체").companyType(CompanyType.SUPPLIER)
                    .hubId(HUB_A).address("주소")
                    .requesterId(REQUESTER_ID).requesterRole("HUB_MANAGER")
                    .requesterHubId(HUB_B) // 다른 허브 — 불일치
                    .build();

            // when & then
            assertThatThrownBy(() -> companyService.createCompany(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(CompanyErrorCode.COMPANY_HUB_MISMATCH.getCode()));
        }
    }

    //updateCompany
    @Nested
    @DisplayName("업체 수정")
    class UpdateCompany {

        @Test
        @DisplayName("MASTER는 모든 업체 수정 가능")
        void master_canUpdateAnyCompany() {
            // given
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.of(company));
            UpdateCompanyCommand command = updateCommand("MASTER", HUB_A, COMPANY_ID);

            assertThatCode(() -> companyService.updateCompany(command))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("HUB_MANAGER는 자신의 허브 업체 수정 가능")
        void hubManager_canUpdateOwnHubCompany() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.of(company)); // company.hubId = HUB_A
            UpdateCompanyCommand command = updateCommand("HUB_MANAGER", HUB_A, null);

            assertThatCode(() -> companyService.updateCompany(command))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("HUB_MANAGER가 다른 허브 업체 수정 시 예외 발생")
        void hubManager_cannotUpdateOtherHubCompany() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.of(company)); // company.hubId = HUB_A
            UpdateCompanyCommand command = updateCommand("HUB_MANAGER", HUB_B, null); // HUB_B — 불일치

            assertThatThrownBy(() -> companyService.updateCompany(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(CompanyErrorCode.COMPANY_HUB_MISMATCH.getCode()));
        }

        @Test
        @DisplayName("COMPANY_MANAGER는 자신의 업체 수정 가능")
        void companyManager_canUpdateOwnCompany() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.of(company));
            UpdateCompanyCommand command = updateCommand("COMPANY_MANAGER", null, COMPANY_ID);

            assertThatCode(() -> companyService.updateCompany(command))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("COMPANY_MANAGER가 타 업체 수정 시 예외 발생")
        void companyManager_cannotUpdateOtherCompany() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.of(company));
            UpdateCompanyCommand command = updateCommand("COMPANY_MANAGER", null, UUID.randomUUID()); // 다른 업체 ID

            assertThatThrownBy(() -> companyService.updateCompany(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(CompanyErrorCode.COMPANY_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("MASTER가 아닌 역할이 hubId 변경 시 예외 발생")
        void nonMaster_cannotChangeHubId() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.of(company));

            // HUB_MANAGER가 hubId 변경 시도
            UpdateCompanyCommand command = UpdateCompanyCommand.builder()
                    .companyId(COMPANY_ID)
                    .hubId(HUB_B) // hubId 변경 시도
                    .requesterId(REQUESTER_ID)
                    .requesterRole("HUB_MANAGER")
                    .requesterHubId(HUB_A)
                    .build();

            assertThatThrownBy(() -> companyService.updateCompany(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(CompanyErrorCode.COMPANY_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("존재하지 않는 업체 수정 시 예외 발생")
        void updateNotFound() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.empty());
            UpdateCompanyCommand command = updateCommand("MASTER", null, null);

            assertThatThrownBy(() -> companyService.updateCompany(command))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(CompanyErrorCode.COMPANY_NOT_FOUND.getCode()));
        }
    }

    // deleteCompany
    @Nested
    @DisplayName("업체 삭제")
    class DeleteCompany {

        @Test
        @DisplayName("MASTER는 모든 업체 삭제 가능")
        void master_canDeleteAnyCompany() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.of(company));

            assertThatCode(() -> companyService.deleteCompany(COMPANY_ID, REQUESTER_ID, "MASTER", null))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("HUB_MANAGER는 자신의 허브 업체 삭제 가능")
        void hubManager_canDeleteOwnHubCompany() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.of(company)); // company.hubId = HUB_A

            assertThatCode(() -> companyService.deleteCompany(COMPANY_ID, REQUESTER_ID, "HUB_MANAGER", HUB_A))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("HUB_MANAGER가 다른 허브 업체 삭제 시 예외 발생")
        void hubManager_cannotDeleteOtherHubCompany() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.of(company)); // company.hubId = HUB_A

            assertThatThrownBy(() -> companyService.deleteCompany(COMPANY_ID, REQUESTER_ID, "HUB_MANAGER", HUB_B))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(CompanyErrorCode.COMPANY_HUB_MISMATCH.getCode()));
        }

        @Test
        @DisplayName("COMPANY_MANAGER는 삭제 불가 — 예외 발생")
        void companyManager_cannotDelete() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.of(company));

            assertThatThrownBy(() -> companyService.deleteCompany(COMPANY_ID, REQUESTER_ID, "COMPANY_MANAGER", null))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(CompanyErrorCode.COMPANY_ACCESS_DENIED.getCode()));
        }

        @Test
        @DisplayName("존재하지 않는 업체 삭제 시 예외 발생")
        void deleteNotFound() {
            given(companyRepository.findByIdAndDeletedAtIsNull(COMPANY_ID))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> companyService.deleteCompany(COMPANY_ID, REQUESTER_ID, "MASTER", null))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(CompanyErrorCode.COMPANY_NOT_FOUND.getCode()));
        }
    }

    // UpdateCompanyCommand 생성 헬퍼 — 반복 코드 제거용
    private UpdateCompanyCommand updateCommand(String role, UUID requesterHubId, UUID requesterCompanyId) {
        return UpdateCompanyCommand.builder()
                .companyId(COMPANY_ID)
                .requesterId(REQUESTER_ID)
                .requesterRole(role)
                .requesterHubId(requesterHubId)
                .requesterCompanyId(requesterCompanyId)
                .build();
    }
}
