package com.sparta.lucky.notification.presentation;

import com.sparta.lucky.notification.application.NotificationService;
import com.sparta.lucky.notification.application.dto.SendOrderAlertCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Internal", description = "내부 서비스 전용 API")
@RestController
@RequestMapping("/internal/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationInternalController {

    private final NotificationService notificationService;

    @Operation(summary = "[내부] 주문 알림 발송", description = "order-service가 주문 생성 시 호출")
    @PostMapping("/order-alert")
    public ResponseEntity<Void> sendOrderAlert(
            @RequestBody SendOrderAlertCommand request,
            @RequestHeader(value = "X-Internal-Request", required = false) String internalRequest
    ) {
        notificationService.sendOrderAlert(request);
        return ResponseEntity.ok().build();
    }
}