package com.sparta.lucky.deliveryservice.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, columnDefinition = "uuid")
    protected UUID createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by", columnDefinition = "uuid")
    protected UUID updatedBy;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @Column(name = "deleted_by", columnDefinition = "uuid")
    protected UUID deletedBy;

    // 삭제 여부 확인
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
