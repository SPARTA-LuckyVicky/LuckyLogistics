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
    // TODO: delivery-service 배송 취소 API 아직은 미구현 상태 -> 구현될시 주석해제
    // @DeleteMapping("/internal/api/v1/deliveries/{deliveryId}")
    // FeignApiResponse<Void> cancelDelivery(
    //         @PathVariable UUID deliveryId,
    //         @RequestHeader("X-Internal-Request") String internalRequest
    // );
}