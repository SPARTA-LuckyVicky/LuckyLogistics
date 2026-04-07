package com.sparta.lucky.deliveryservice.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HubRouteResponse {

    private HubRouteData data;

    // 기존 호출부(DeliveryProcessingService, DeliveryRouteService) 변경 없이 호환
    public List<RouteItem> route() {
        return data != null ? data.route : null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HubRouteData {
        public List<RouteItem> route;
    }

    // record 유지 → DeliveryRouteCreateDto의 HubRouteResponse.RouteItem import 호환
    public record RouteItem(
        UUID fromHubId,
        UUID toHubId,
        Long expectedDuration,
        Long expectedDistance
    ) {}
}
