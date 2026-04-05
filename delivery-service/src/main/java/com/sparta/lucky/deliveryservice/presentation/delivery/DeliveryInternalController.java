package com.sparta.lucky.deliveryservice.presentation.delivery;

import com.sparta.lucky.deliveryservice.application.DeliveryService;
import com.sparta.lucky.deliveryservice.common.response.CommonApiResponse;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.presentation.delivery.payload.DeliveryCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryInternalController {

    private final DeliveryService deliveryService;

    @Operation(summary="배송 생성", description="배송 데이터를 생성합니다.<br>MASTER권한만 사용 가능합니다.")
    @PostMapping
    public ResponseEntity<CommonApiResponse<UUID>> createDelivery(
        @RequestBody @Valid DeliveryCreateRequest deliveryCreateRequest
    ) {
        UUID deliveryId = deliveryService.createDelivery(deliveryCreateRequest.toCommand());
        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.DELIVERY_CREATED, deliveryId));
    }
}
