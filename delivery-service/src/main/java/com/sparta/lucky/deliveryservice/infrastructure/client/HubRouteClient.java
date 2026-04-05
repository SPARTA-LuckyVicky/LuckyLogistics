package com.sparta.lucky.deliveryservice.infrastructure.client;

import com.sparta.lucky.deliveryservice.infrastructure.client.dto.HubRouteResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="hub-service")
public interface HubRouteClient {

    @GetMapping("/internal/v1/paths")
    HubRouteResponse getHubRoute(
        @RequestParam("originHubId") UUID originHubId,
        @RequestParam("destinationHubId") UUID destinationHubId
    );
}
