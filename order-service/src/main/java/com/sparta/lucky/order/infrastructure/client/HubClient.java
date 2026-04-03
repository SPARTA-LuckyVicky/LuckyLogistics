package com.sparta.lucky.order.infrastructure.client;

import com.sparta.lucky.order.infrastructure.client.dto.FeignApiResponse;
import com.sparta.lucky.order.infrastructure.client.dto.HubResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/internal/api/v1/hubs/{hubId}")
    FeignApiResponse<HubResponse> getHub(@PathVariable UUID hubId);
}
