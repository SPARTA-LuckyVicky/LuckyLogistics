package com.sparta.lucky.deliveryservice.domain.driver;

import com.sparta.lucky.deliveryservice.domain.driver.code.DriverStatus;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_delivery_driver", schema = "delivery_schema")
public class DeliveryDriver {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

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
}
