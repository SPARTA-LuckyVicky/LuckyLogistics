package com.sparta.lucky.notification.infrastructure.client;

import com.sparta.lucky.notification.infrastructure.client.dto.HubResponse;
import com.sparta.lucky.notification.infrastructure.client.dto.FeignApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/internal/api/v1/hubs/{hubId}")
    FeignApiResponse<HubResponse> getHub(
            @PathVariable UUID hubId,
            @RequestHeader("X-Internal-Request") String internalRequest
    );
}