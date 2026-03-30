package com.sparta.lucky.deliveryservice.delivery.domain;

import com.sparta.lucky.deliveryservice.delivery.code.DeliveryStatus;
import com.sparta.lucky.deliveryservice.driver.domain.DeliveryDriver;
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
public class Delivery {

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
}
