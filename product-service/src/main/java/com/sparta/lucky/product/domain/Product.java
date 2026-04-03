package com.sparta.lucky.product.domain;

import com.sparta.lucky.product.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;

import java.util.UUID;

@Entity
@Table(name = "p_product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(columnDefinition = "uuid", nullable = false)
    private UUID companyId; // 상품 소속 업체

    @Column(columnDefinition = "uuid", nullable = false)
    private UUID hubId; // 상품 소속 허브 ID

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private ProductStatus  status;

    // ProductStock이 연관관계 주인 (product_id 컬럼을 ProductStock이 가짐)
    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY)
    private ProductStock stock;

    // 도메인 메서드

    /**
     * 기본 정보 수정 (MASTER, HUB_MANAGER, COMPANY_MANAGER 공통)
     * null 값은 무시 — 전달된 필드만 업데이트
     */
    public void update(String name, Integer price, ProductStatus status, UUID companyId, UUID hubId) {
        if (name != null) this.name = name;
        if (price != null) this.price = price;
        if (status != null) this.status = status;
        if (companyId != null) this.companyId = companyId;
        if (hubId != null) this.hubId = hubId;
    }

}
