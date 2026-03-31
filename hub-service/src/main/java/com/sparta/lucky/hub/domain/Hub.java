package com.sparta.lucky.hub.domain;

import com.sparta.lucky.hub.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_hub", schema = "hub_schema")
public class Hub extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "manager_id", columnDefinition = "uuid")
    private UUID managerId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    public static Hub create(String name, String address, BigDecimal latitude, BigDecimal longitude) {
        Hub hub = new Hub();
        hub.name = name;
        hub.address = address;
        hub.latitude = latitude;
        hub.longitude = longitude;
        return hub;
    }

    public void update(String name, String address, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void assignManager(UUID managerId) {
        this.managerId = managerId;
    }
}