package com.sparta.lucky.order.presentation;


import com.sparta.lucky.order.application.OrderService;
import com.sparta.lucky.order.application.dto.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Order", description = "주문 관리 내부 API")
@RestController
@RequestMapping("/internal/api/v1/orders")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderService orderService;

    // ===================== 내부 API (서비스 간 통신 전용) =====================

    @Operation(summary = "[내부] 주문 완료 처리", description = "delivery-service가 배송 완료 시 호출")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<OrderResponse> completeOrder(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Internal-Request", required = false) String internalRequest
    ) {
        // TODO: JWT 연동 후 실제 인증 추가
        // 지금은 헤더 존재 여부만 확인
        return ResponseEntity.ok(orderService.completeOrder(id));
    }
}
