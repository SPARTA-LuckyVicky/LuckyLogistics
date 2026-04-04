package com.sparta.lucky.order.infrastructure.client;


import com.sparta.lucky.order.infrastructure.client.dto.FeignApiResponse;
import com.sparta.lucky.order.infrastructure.client.dto.DeliveryCreateRequest;
import com.sparta.lucky.order.infrastructure.client.dto.DeliveryCreateResponse;
import com.sparta.lucky.order.infrastructure.client.dto.DeliveryStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @PostMapping("/internal/api/v1/deliveries")
    FeignApiResponse<DeliveryCreateResponse> createDelivery(@RequestBody DeliveryCreateRequest request,
                                                            @RequestHeader("X-Internal-Request") String internalRequest);

    @GetMapping("/internal/api/v1/deliveries/{orderId}/status")
    FeignApiResponse<DeliveryStatusResponse> getDeliveryStatus(@PathVariable UUID orderId,
                                                               @RequestHeader("X-Internal-Request") String internalRequest);

    @GetMapping("/internal/api/v1/deliveries/driver/{driverId}")
    FeignApiResponse<List<UUID>> getDeliveryIdsByDriver(
            @PathVariable UUID driverId,
            @RequestHeader("X-Internal-Request") String internalRequest
    );
}