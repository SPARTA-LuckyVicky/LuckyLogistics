package com.sparta.lucky.product.domain;

import com.sparta.lucky.product.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_product_stock",schema = "product_schema")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStock extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    // UUID productId -> JPA관계로 대체
    // @JoinColumn이 product_id 컬럼을 생성
    // product 목록 검색시 N+1 문제를 방지하기 위함
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private Product product;

    @Column(columnDefinition = "uuid", nullable = false)
    private UUID hubId; // 상품 소속 허브 ID

    @Column(nullable = false)
    private Integer stock;

    @Version // 낙관적 락 - 충돌시 OptimisticLockException 발생
    @Column(nullable = false)
    private Long version;

    // 재고 절대값으로 수정 (외부 API용)
    // 도메인 불변식: 재고는 반드시 0 이상
    public void updateStock(Integer newStock) {
        if (newStock == null || newStock < 0) {
            throw new IllegalArgumentException("재고는 0 이상이어야 합니다. 입력값: " + newStock);
        }
        this.stock = newStock;
    }

}
