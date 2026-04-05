package com.sparta.lucky.deliveryservice.presentation.delivery;

import com.sparta.lucky.deliveryservice.application.policy.DeliveryReadService;
import com.sparta.lucky.deliveryservice.application.service.DeliveryService;
import com.sparta.lucky.deliveryservice.common.code.Role;
import com.sparta.lucky.deliveryservice.common.response.CommonApiResponse;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.common.security.auth.ExternalUserPrincipal;
import com.sparta.lucky.deliveryservice.presentation.delivery.payload.DeliveryReadPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryReadService deliveryReadService;

    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<CommonApiResponse<?>> delete(
        @PathVariable UUID deliveryId,
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {
        deliveryService.deleteDelivery(deliveryId, user.getUserId(), user.getRole());
        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK));
    }

    @Operation(summary = "배송 목록 조회", description = "배송 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonApiResponse<DeliveryReadPageResponse>> getDeliveries(
        @PageableDefault(page = 0, size = 10) Pageable pageable,
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {
        DeliveryReadPageResponse response = null;

        // branching by role
        if(user.getRole().equals(Role.MASTER)) {
            response = DeliveryReadPageResponse.from(
                deliveryReadService.getAllDeliveries(pageable)
            );
        }
        else if(user.getRole().equals(Role.HUB_MANAGER)) {
            response = DeliveryReadPageResponse.from(deliveryReadService.getHubDeliveries(pageable, user.getUserId()));
        }
        else if(user.getRole().equals(Role.DELIVERY_DRIVER)) {
            response = DeliveryReadPageResponse.from(deliveryReadService.getDriverDeliveries(pageable, user.getUserId()));
        }
        else return ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonApiResponse.error(ResponseCode.FORBIDDEN));

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK, response));
    }
}
