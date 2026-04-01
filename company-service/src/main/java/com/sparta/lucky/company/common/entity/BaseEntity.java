package com.sparta.lucky.company.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 내부 API/시스템 컨텍스트에서 X-User-Id 헤더 없이 저장 시 NOT NULL 위반 방지
    @CreatedBy
    @Column(name = "created_by", nullable = true, updatable = false, columnDefinition = "uuid")
    private UUID createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by", columnDefinition = "uuid")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", columnDefinition = "uuid")
    private UUID deletedBy;

    // Soft Delete 처리 메서드 — 상속받는 엔티티에서 호출
    // 재호출 시 최초 감사정보(삭제일시/삭제자) 보호를 위해 이미 삭제된 경우 early return
    public void softDelete(UUID deletedBy) {
        if (this.deletedAt != null) {
            return;
        }
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = Objects.requireNonNull(deletedBy, "deletedBy 값 누락");
    }

    // 삭제 여부 확인
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}