package com.sparta.lucky.hub.domain;

import com.sparta.lucky.hub.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_hub_route", schema = "hub_schema")
public class HubRoute extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "origin_hub_id", nullable = false, columnDefinition = "uuid")
    private UUID originHubId;

    @Column(name = "destination_hub_id", nullable = false, columnDefinition = "uuid")
    private UUID destinationHubId;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "distance", nullable = false)
    private Integer distance;

    public void updateRouteInfo(int distance, int duration) {
        this.distance = distance;
        this.duration = duration;
    }
}