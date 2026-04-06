package com.sparta.lucky.deliveryservice.presentation.delivery;

import com.sparta.lucky.deliveryservice.application.DeliveryService;
import com.sparta.lucky.deliveryservice.common.code.Role;
import com.sparta.lucky.deliveryservice.common.response.CommonApiResponse;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.common.security.auth.ExternalUserPrincipal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<CommonApiResponse<?>> delete(
        @PathVariable UUID deliveryId,
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {
        deliveryService.deleteDelivery(deliveryId, user.getUserId(), user.getRole());
        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK));
    }
}
