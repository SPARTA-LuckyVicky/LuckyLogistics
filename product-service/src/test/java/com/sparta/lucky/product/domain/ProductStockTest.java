package com.sparta.lucky.product.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ProductStock 도메인 단위 테스트
 * updateStock()의 불변식 ("재고는 0 이상") 검증에 집중
 * 프레임워크(JPA, Mockito) 없이 순수 Java로 실행
 */
class ProductStockTest {

    private ProductStock stock;

    @BeforeEach
    void setUp() {
        // 초기 재고 10으로 고정
        stock = ProductStock.builder()
                .stock(10)
                .build();
    }

    @Test
    @DisplayName("null 입력 시 IllegalArgumentException")
    void updateStock_null_throwsException() {
        assertThatThrownBy(() -> stock.updateStock(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("음수 입력 시 IllegalArgumentException")
    void updateStock_negative_throwsException() {
        assertThatThrownBy(() -> stock.updateStock(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("0 입력 시 정상 적용 - 재고 소진 상태 허용")
    void updateStock_zero_success() {
        // 0은 재고 소진을 의미하며 유효한 상태다
        stock.updateStock(0);

        assertThat(stock.getStock()).isEqualTo(0);
    }

    @Test
    @DisplayName("양수 입력 시 정상 적용")
    void updateStock_positive_success() {
        stock.updateStock(99);

        assertThat(stock.getStock()).isEqualTo(99);
    }
}