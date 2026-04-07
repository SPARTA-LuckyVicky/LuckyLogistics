package com.sparta.lucky.deliveryservice.domain.delivery;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryRouteCreateDto;
import com.sparta.lucky.deliveryservice.common.entity.BaseEntity;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryRouteStatus;
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
@Table(name = "p_delivery_route", schema = "delivery_schema")
public class DeliveryRoute extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="delivery_id", updatable = false, nullable = false)
    private Delivery delivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="hub_driver_id", nullable = false)
    private DeliveryDriver deliveryDriver;

    @Column(name="sequence", nullable = false)
    private Integer sequence;

    @Column(name="from_hub_id", nullable = false)
    private UUID fromHubId;

    @Column(name="to_hub_id", nullable = false)
    private UUID toHubId;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private DeliveryRouteStatus status;

    @Column(name="expected_distance", updatable = false,nullable = false)
    private Long expectedDistance;

    @Column(name="expected_duration_seconds", updatable = false, nullable = false)
    private Long expectedDurationSeconds;

    @Column(name="actual_distance")
    private Long actualDistance;

    @Column(name="actual_duration_seconds")
    private Long actualDurationSeconds;

    @Column(name="started_at")
    private LocalDateTime startedAt;

    @Column(name="arrived_at")
    private LocalDateTime arrivedAt;



    // Factory methods ================================================================
    public static DeliveryRoute create(DeliveryRouteCreateDto dto) {
        DeliveryRoute deliveryRoute = new DeliveryRoute();
        deliveryRoute.delivery = dto.delivery();
        deliveryRoute.deliveryDriver = dto.driver();
        deliveryRoute.sequence = dto.sequence();
        deliveryRoute.fromHubId = dto.fromHubId();
        deliveryRoute.toHubId = dto.toHubId();
        deliveryRoute.status = DeliveryRouteStatus.WAITING;
        deliveryRoute.expectedDistance = dto.expectedDistance();
        deliveryRoute.expectedDurationSeconds = dto.expectedDurationSeconds();
        return deliveryRoute;
    }

    public void softDelete(UUID accessId) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = accessId;
    }
}
