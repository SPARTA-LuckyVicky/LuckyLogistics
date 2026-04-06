package com.sparta.lucky.hub.domain;

import com.sparta.lucky.hub.common.entity.BaseEntity;
import com.sparta.lucky.hub.common.exception.BusinessException;
import com.sparta.lucky.hub.common.exception.HubErrorCode;
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

    public static HubRoute create(UUID originHubId, UUID destinationHubId, int distance, int duration) {
        HubRoute route = new HubRoute();
        route.originHubId = originHubId;
        route.destinationHubId = destinationHubId;
        route.distance = distance;
        route.duration = duration;
        return route;
    }

    /** 그래프 탐색 전용 역방향 인스턴스 생성 (DB 저장 X) */
    public HubRoute reverse() {
        HubRoute reversed = new HubRoute();
        reversed.originHubId = this.destinationHubId;
        reversed.destinationHubId = this.originHubId;
        reversed.distance = this.distance;
        reversed.duration = this.duration;
        return reversed;
    }

    public void updateRouteInfo(int distance, int duration) {
        if (distance < 0 || duration < 0) {
            throw new BusinessException(HubErrorCode.HUB_ROUTE_INVALID_VALUE);
        }
        this.distance = distance;
        this.duration = duration;
    }
}