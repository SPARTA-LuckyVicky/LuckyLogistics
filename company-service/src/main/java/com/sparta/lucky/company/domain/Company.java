package com.sparta.lucky.company.domain;

import com.sparta.lucky.company.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;

import java.util.UUID;

@Entity
@Table(name = "p_company")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Company extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    // DB에 "SUPPLIER" / "RECEIVER" 문자열로 저장
    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", length = 10, nullable = false)
    private CompanyType companyType;

    // Cross-service FK 금지 — UUID 값만 저장, 무결성은 FeignClient로 검증
    @Column(name = "hub_id", nullable = false, columnDefinition = "uuid")
    private UUID hubId;

    // MASTER가 user-service 내부 API로 나중에 배정 → 생성 시 NULL 허용
    @Column(name = "manager", columnDefinition = "uuid")
    private UUID manager;

    @Column(name = "address", length = 255, nullable = false)
    private String address;

    // 도메인 메서드

    /**
     * 기본 정보 수정 (MASTER, HUB_MANAGER, COMPANY_MANAGER 공통)
     * null 값은 무시 — 전달된 필드만 업데이트
     */
    public void update(String name, CompanyType companyType, String address) {
        if (name != null) this.name = name;
        if (companyType != null) this.companyType = companyType;
        if (address != null) this.address = address;
    }

    /**
     * 허브 변경 — MASTER 전용
     * hub_id 변경 시 product/product_stock의 hub_id는 동기화되지 않음
     */
    public void changeHub(UUID newHubId) {
        // null이 들어오면 DB 저장 시점에 실패하지 않고 즉시 실패로 수정
        this.hubId = Objects.requireNonNull(newHubId, "newHubId 값 누락");
    }

    /**
     * 담당자 배정 — user-service가 내부 API로 호출
     */
    public void assignManager(UUID managerId) {
        this.manager = managerId;
    }
}