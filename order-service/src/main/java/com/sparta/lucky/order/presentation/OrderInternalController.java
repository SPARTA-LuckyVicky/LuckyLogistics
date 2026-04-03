package com.sparta.lucky.order.presentation;


import com.sparta.lucky.order.application.OrderService;
import com.sparta.lucky.order.application.dto.OrderInternalResponse;
import com.sparta.lucky.order.application.dto.OrderResponse;
import com.sparta.lucky.order.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Order", description = "주문 관리 내부 API")
@RestController
@RequestMapping("/internal/api/v1/orders")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderService orderService;
    private static final String INTERNAL_REQUEST = "true";
    // ===================== 내부 API (서비스 간 통신 전용) =====================

    @Operation(summary = "[내부] 주문 완료 처리", description = "delivery-service가 배송 완료 시 호출")
    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<ApiResponse<OrderResponse>> completeOrder(
            @PathVariable UUID orderId,
            @RequestHeader(value = "X-Internal-Request") String internalRequest
    ) {
        if (!INTERNAL_REQUEST.equals(internalRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ApiResponse.success(orderService.completeOrder(orderId)));
    }

    @Operation(summary = "[내부] 주문 단건 조회", description = "notification-service가 주문 정보 조회 시 호출")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderInternalResponse>> getOrder(
            @PathVariable UUID orderId,
            @RequestHeader(value = "X-Internal-Request") String internalRequest
    ) {
        if (!INTERNAL_REQUEST.equals(internalRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderInternal(orderId)));
    }

}
