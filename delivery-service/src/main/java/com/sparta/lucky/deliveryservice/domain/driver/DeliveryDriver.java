package com.sparta.lucky.deliveryservice.domain.driver;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverCreateCommand;
import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverUpdateCommand;
import com.sparta.lucky.deliveryservice.common.entity.BaseEntity;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverStatus;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_delivery_driver", schema = "delivery_schema")
public class DeliveryDriver extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

    @Column(name="user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name="hub_id", nullable = false)
    private UUID hubId;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private DriverType type;

    @Column(name="assignment_order", updatable = false, nullable = false, insertable = false)
    private Integer assignmentOrder;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private DriverStatus status;


    // Factory Methods ====================================================

    /**
     * 배송 담당자 객체를 생성합니다.<br>
     * 새로 생성되는 배송 담당자의 status는 {@code IDLE}입니다.
     * @param command 생성할 배송 담당자의 정보를 담은 dto
     * @return {@link DeliveryDriver}
     */
    public static DeliveryDriver create(DeliveryDriverCreateCommand command, Integer assignmentOrder) {
        DeliveryDriver driver = new DeliveryDriver();
        driver.userId = command.driverId();
        driver.hubId = command.hubId();
        driver.type = command.type();
        driver.assignmentOrder = assignmentOrder;
        driver.status = DriverStatus.IDLE;
        return driver;
    }

    /**
     * 배송 담당자 데이터를 soft delete합니다.
     * @param accessId 요청한 사용자의 ID
     */
    public void softDelete(UUID accessId) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = accessId;
    }

    public void update(DeliveryDriverUpdateCommand command) {
        this.hubId = command.hubId() != null ? command.hubId() : this.hubId;
        this.type = command.type() != null ? command.type() : this.type;
        this.status = command.status() != null ? command.status() : this.status;
    }

    /**
     * 배송 기사의 상태를 업데이트 합니다.
     * @param driverStatus 변경할 상태값
     */
    public void updateStatus(DriverStatus driverStatus) {
        this.status = driverStatus;
    }
}
