package com.sparta.lucky.company.presentation;

import com.sparta.lucky.company.application.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CompanyController 웹 계층 테스트
 * 서비스 레이어와 독립적인 컨트롤러 고유 로직만 검증
 * - validateHubHeader : HUB_MANAGER의 X-Hub-Id 헤더 필수 검증
 * - 페이지네이션 size 정규화 : 허용값(10/30/50) 외 요청 시 10으로 고정
 */
@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyService companyService;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID HUB_ID = UUID.randomUUID();

    @Nested
    @DisplayName("HUB_MANAGER X-Hub-Id 헤더 검증")
    class ValidateHubHeader {

        @Test
        @DisplayName("HUB_MANAGER가 X-Hub-Id 헤더 없이 업체 생성 요청 시 403 반환")
        void hubManager_withoutHubIdHeader_returns403() throws Exception {
            // given
            mockMvc.perform(post("/api/v1/companies")
                            .header("X-User-Id", USER_ID.toString())
                            .header("X-User-Role", "HUB_MANAGER")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                      {
                                          "name": "테스트업체",
                                          "companyType": "SUPPLIER",
                                          "hubId": "%s",
                                          "address": "서울시 테스트구 테스트로 1"
                                      }
                                      """.formatted(HUB_ID)))
                    // then: 서비스 호출 전 컨트롤러에서 차단 → 403
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("COMPANY_003"));
        }
    }

    @Nested
    @DisplayName("페이지네이션 size 정규화")
    class PaginationSize {

        @BeforeEach
        void setUp() {
            // 페이지 조회 결과는 본 테스트의 관심사가 아니므로 빈 페이지로 stub
            given(companyService.getCompanies(any(), any(), any(), any()))
                    .willReturn(Page.empty());
        }

        @Test
        @DisplayName("허용되지 않는 size(7) 요청 시 서비스에는 size=10으로 정규화되어 전달")
        void getCompanies_invalidSize_normalizedTo10() throws Exception {
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            mockMvc.perform(get("/api/v1/companies").param("size", "7"))
                    .andExpect(status().isOk());

            // 서비스에 전달된 Pageable의 size가 10으로 정규화됐는지 검증
            verify(companyService).getCompanies(any(), any(), any(), pageableCaptor.capture());
            assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("허용된 size(30) 요청 시 서비스에 그대로 size=30 전달")
        void getCompanies_validSize30_kept() throws Exception {
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            mockMvc.perform(get("/api/v1/companies").param("size", "30"))
                    .andExpect(status().isOk());

            // 정규화 없이 30 그대로 전달됐는지 검증
            verify(companyService).getCompanies(any(), any(), any(), pageableCaptor.capture());
            assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(30);
        }
    }
}