package com.sparta.lucky.product.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.lucky.product.application.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ProductController / ProductInternalController 슬라이스 테스트
 *
 * 검증 대상
 * 1. validateHubHeader — HUB_MANAGER + X-Hub-Id 누락 → 403 (서비스 레이어 NPE 방지 로직)
 * 2. 페이지 크기 정규화 — [10, 30, 50] 외 값은 10으로 보정
 * 3. validateInternalRequest — X-Internal-Request 누락/오류 → 400/403
 */
@WebMvcTest(controllers = {ProductController.class, ProductInternalController.class})
@AutoConfigureMockMvc(addFilters = false)  // Security 필터 비활성화 - 실제 인증은 Gateway 처리
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private static final UUID USER_ID    = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();

    @Nested
    @DisplayName("HUB_MANAGER + X-Hub-Id 누락 → 403 PRODUCT_NOT_ALLOWED")
    class ValidateHubHeader {

        @Test
        @DisplayName("상품 목록 조회: X-Hub-Id 없으면 403")
        void getProducts_hubManagerWithoutHubId_returns403() throws Exception {
            // HUB_MANAGER 역할인데 X-Hub-Id 헤더를 보내지 않은 경우
            // → validateHubHeader에서 BusinessException(PRODUCT_NOT_ALLOWED) 발생 → 403
            mockMvc.perform(get("/api/v1/products")
                            .header("X-User-Id", USER_ID)
                            .header("X-User-Role", "HUB_MANAGER"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("PRODUCT_004"));
        }

        @Test
        @DisplayName("상품 단건 조회: X-Hub-Id 없으면 403")
        void getProduct_hubManagerWithoutHubId_returns403() throws Exception {
            mockMvc.perform(get("/api/v1/products/{id}", PRODUCT_ID)
                            .header("X-User-Id", USER_ID)
                            .header("X-User-Role", "HUB_MANAGER"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("PRODUCT_004"));
        }
    }

    @Nested
    @DisplayName("페이지 크기 정규화 — 허용값 [10, 30, 50] 외에는 10으로 보정")
    class PaginationNormalization {

        @BeforeEach
        void stubProductService() {
            // 실제 서비스 결과가 아닌 Pageable 전달 값 검증이 목적이므로 빈 Page 반환
            given(productService.getProducts(any(), any(), any(), any(), any(), any()))
                    .willReturn(Page.empty());
        }

        @Test
        @DisplayName("허용 외 크기 요청 → 서비스에 size=10 으로 정규화되어 전달")
        void invalidSize_normalizedTo10() throws Exception {
            mockMvc.perform(get("/api/v1/products")
                            .header("X-User-Id", USER_ID)
                            .header("X-User-Role", "MASTER")
                            .param("size", "7"))
                    .andExpect(status().isOk());

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(productService).getProducts(
                    isNull(), isNull(), isNull(), eq("MASTER"), isNull(), captor.capture());
            assertThat(captor.getValue().getPageSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("허용 크기(30) 요청 → 서비스에 size=30 그대로 전달")
        void validSize30_kept() throws Exception {
            mockMvc.perform(get("/api/v1/products")
                            .header("X-User-Id", USER_ID)
                            .header("X-User-Role", "MASTER")
                            .param("size", "30"))
                    .andExpect(status().isOk());

            ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
            verify(productService).getProducts(
                    isNull(), isNull(), isNull(), eq("MASTER"), isNull(), captor.capture());
            assertThat(captor.getValue().getPageSize()).isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("내부 API — X-Internal-Request 헤더 검증")
    class ValidateInternalRequest {

        @Test
        @DisplayName("X-Internal-Request 헤더 누락 → 400 VALIDATION_003")
        void missingHeader_returns400() throws Exception {
            // required 헤더 누락 → MissingRequestHeaderException → GlobalExceptionHandler → 400
            mockMvc.perform(get("/internal/api/v1/products/{id}", PRODUCT_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_003"));
        }

        @Test
        @DisplayName("X-Internal-Request: false → 403 PRODUCT_ACCESS_DENIED")
        void wrongValue_returns403() throws Exception {
            // 헤더 존재하지만 "true"가 아닌 경우
            // → validateInternalRequest에서 BusinessException(PRODUCT_ACCESS_DENIED) → 403
            mockMvc.perform(get("/internal/api/v1/products/{id}", PRODUCT_ID)
                            .header("X-Internal-Request", "false"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value("PRODUCT_003"));
        }
    }
}