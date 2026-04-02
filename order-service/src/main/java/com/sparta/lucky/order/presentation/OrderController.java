package com.sparta.lucky.order.presentation;

import com.sparta.lucky.order.application.OrderService;
import com.sparta.lucky.order.application.dto.OrderResponse;
import com.sparta.lucky.order.common.response.ApiResponse;
import com.sparta.lucky.order.domain.OrderStatus;
import com.sparta.lucky.order.presentation.dto.PostOrderReqDto;
import com.sparta.lucky.order.presentation.dto.PatchOrderReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Order", description = "주문 관리 API")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody @Valid PostOrderReqDto request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.createOrder(request.toCommand())));
    }

    @Operation(summary = "주문 목록 조회", description = "status 필터 가능. 페이지 사이즈: 10 / 30 / 50 (그 외는 10으로 고정)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        // 허용 페이지 사이즈: 10, 30, 50 — 그 외는 10으로 고정
        int pageSize = pageable.getPageSize();
        if (pageSize != 10 && pageSize != 30 && pageSize != 50) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(), 10, pageable.getSort()
            );
        }
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrders(status, pageable)));
    }

    @Operation(summary = "주문 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrder(id)));
    }

    @Operation(summary = "주문 수정", description = "requestNote, requestedDeadline 수정 가능 (CREATED 상태만)")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(
            @PathVariable UUID id,
            @RequestBody @Valid PatchOrderReqDto request
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderService.updateOrder(id, request.toCommand())));
    }

    @Operation(summary = "주문 취소", description = "상태를 CANCELLED로 변경 (CREATED 상태만 가능, 노출 유지)")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrder(id)));
    }

    @Operation(summary = "주문 삭제", description = "Soft delete (CANCELLED / COMPLETED 상태만 가능)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = true) UUID deletedBy
    ) {
        orderService.deleteOrder(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
