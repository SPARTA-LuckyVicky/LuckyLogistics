package com.sparta.lucky.deliveryservice.driver.domain;

import com.sparta.lucky.deliveryservice.driver.code.DriverStatus;
import com.sparta.lucky.deliveryservice.driver.code.DriverType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_delivery_driver")
public class DeliveryDriver {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

    @Column(name="hub_id", nullable = false)
    private UUID hubId;

    @Column(name="type", nullable = false)
    private DriverType type;

    @SequenceGenerator(
        name="DRIVER_SEQUENCE_GENERATOR",
        sequenceName = "DRIVER_SEQUENCE_GENERATOR"
    )
    @Column(name="assignment_order", updatable = false, nullable = false)
    private Integer assignmentSequence;

    @Column(name="status", nullable = false)
    private DriverStatus status;
}
