package com.sparta.lucky.deliveryservice.presentation.delivery;

import com.sparta.lucky.deliveryservice.application.service.DeliveryReadService;
import com.sparta.lucky.deliveryservice.application.service.DeliveryService;
import com.sparta.lucky.deliveryservice.common.response.CommonApiResponse;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryStatus;
import com.sparta.lucky.deliveryservice.presentation.delivery.payload.DeliveryCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryInternalController {

    private final DeliveryService deliveryService;
    private final DeliveryReadService deliveryReadService;

    @Operation(summary="배송 생성", description="배송 데이터를 생성합니다.<br>주문 생성시 호출합니다.")
    @PostMapping
    public ResponseEntity<CommonApiResponse<UUID>> createDelivery(
        @RequestBody @Valid DeliveryCreateRequest deliveryCreateRequest
    ) {
        UUID deliveryId = deliveryService.createDelivery(deliveryCreateRequest.toCommand());
        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.DELIVERY_CREATED, deliveryId));
    }

    @Operation(summary = "배송 상태 확인", description = "배송 상태를 응답합니다.")
    @GetMapping("/{orderId}/status")
    public ResponseEntity<CommonApiResponse<DeliveryStatus>> getDeliveryStatus(
        @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(
            CommonApiResponse.success(
                ResponseCode.OK,
                deliveryReadService.getStatusByOrderId(orderId)
            )
        );
    }
}
