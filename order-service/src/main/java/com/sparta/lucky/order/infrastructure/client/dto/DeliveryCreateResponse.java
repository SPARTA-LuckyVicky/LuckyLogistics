package com.sparta.lucky.order.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class DeliveryCreateResponse {
    private UUID deliveryId;

    // 경로 정보 (hub-service 경로 탐색 결과)
    private Integer totalDuration;      // 총 소요시간 (분)
    private Integer totalDistance;      // 총 거리 (km)
    private List<UUID> route;           // 경유 허브 UUID 목록

}