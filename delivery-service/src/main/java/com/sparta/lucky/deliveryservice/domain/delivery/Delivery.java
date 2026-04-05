package com.sparta.lucky.deliveryservice.domain.delivery;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryCreateCommand;
import com.sparta.lucky.deliveryservice.common.entity.BaseEntity;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryStatus;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "p_delivery", schema = "delivery_schema")
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_driver_id")
    private DeliveryDriver deliveryDriver;

    @Column(name="order_id", updatable = false, nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private DeliveryStatus status;

    @Column(name="origin_hub", nullable = false)
    private UUID originHub;

    @Column(name="current_hub")
    private UUID currentHub;

    @Column(name="destination_hub", nullable = false)
    private UUID destinationHub;

    @Column(name="delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name="recipient_name", nullable = false)
    private String recipientName;

    @Column(name="recipient_slack_id", nullable = false)
    private String recipientSlackId;

    @Column(name="started_at")
    private LocalDateTime startedAt;

    @Column(name="arrived_at")
    private LocalDateTime arrivedAt;

    // Factory Methods ====================================================

    /**
     * 최소한의 정보로 배송 정보를 생성합니다.<br>
     * 업체의 소속 HubId와 주소가 별도로 필요합니다.
     * @param command 배송 정보 생성을 위한 데이터
     * @param companyHubId 업체가 소속된 Hub의 ID
     * @param companyAddress 업체의 실제 주소
     * @return {@code Delivery}
     */
    public static Delivery create(DeliveryCreateCommand command, UUID companyHubId, String companyAddress) {
        Delivery delivery = new Delivery();
        delivery.orderId = command.orderId();
        delivery.status = DeliveryStatus.PENDING;
        delivery.originHub = command.originHubId();
        delivery.currentHub = command.originHubId();
        delivery.destinationHub = companyHubId;
        delivery.deliveryAddress = companyAddress;
        delivery.recipientName = command.recipientName();
        delivery.recipientSlackId = command.recipientSlackId();
        return delivery;
    }

    /**
     * 배송 정보에서 업체 배송담당자에 대한 정보를 변경합니다.
     * @param driver 변경할 업체 배송 담당자 객체
     */
    public void updateDriver(DeliveryDriver driver) {
        this.deliveryDriver = driver;
    }

    /**
     * 배송 정보에서 배송 상태에 대한 데이터를 변경합니다.
     * @param status 변경할 배송상태
     */
    public void updateStatus(DeliveryStatus status) {
        this.status = status;
    }
}
