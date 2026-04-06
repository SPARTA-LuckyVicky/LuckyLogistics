package com.sparta.lucky.company.infrastructure.feign;

import com.sparta.lucky.company.infrastructure.feign.dto.FeignApiResponse;
import com.sparta.lucky.company.infrastructure.feign.dto.HubResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

// Eureka 서비스명으로 로드밸런싱
@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/internal/api/v1/hubs/{hubId}")
    FeignApiResponse<HubResponse> getHub(
            @PathVariable("hubId") UUID hubId,
            @RequestHeader("X-Internal-Request") String internalRequest
    );
}