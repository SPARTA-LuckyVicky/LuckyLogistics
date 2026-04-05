package com.sparta.lucky.deliveryservice.presentation.delivery;

import com.sparta.lucky.deliveryservice.application.service.DeliveryRouteReadService;
import com.sparta.lucky.deliveryservice.common.code.Role;
import com.sparta.lucky.deliveryservice.common.error.exceptions.ForbiddenException;
import com.sparta.lucky.deliveryservice.common.response.CommonApiResponse;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.common.security.auth.ExternalUserPrincipal;
import com.sparta.lucky.deliveryservice.presentation.delivery.payload.DeliveryRouteReadPageResponse;
import com.sparta.lucky.deliveryservice.presentation.delivery.payload.DeliveryRouteReadResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/deliveryies/{deliveryId}/routes")
@RequiredArgsConstructor
public class DeliveryRouteController {

    private final DeliveryRouteReadService deliveryRouteReadService;

    @Operation(summary = "배송 경로 목록 조회", description = "배송 경로 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonApiResponse<DeliveryRouteReadPageResponse>> getDeliveryRoutes(
        @PathVariable UUID deliveryId,
        @PageableDefault(page = 0, size = 10) Pageable pageable,
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {
        DeliveryRouteReadPageResponse response = null;

        if(user.getRole().equals(Role.MASTER)) {
            response = DeliveryRouteReadPageResponse.from(
                deliveryRouteReadService.getDeliveryRoutes(pageable, deliveryId)
            );
        } else if(user.getRole().equals(Role.HUB_MANAGER)) {
            response = DeliveryRouteReadPageResponse.from(
                deliveryRouteReadService.getHubDeliveryRoutes(pageable, deliveryId, user.getUserId())
            );
        } else if(user.getRole().equals(Role.DELIVERY_DRIVER)) {
            response = DeliveryRouteReadPageResponse.from(
                deliveryRouteReadService.getDriverDeliveryRoutes(pageable, deliveryId, user.getUserId())
            );
        } else throw new ForbiddenException(ResponseCode.FORBIDDEN);

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK, response));
    }

    @Operation(summary = "배송 경로 단건 조회", description = "배송 경로를 단건 조회합니다.")
    @GetMapping("/{sequence}")
    public ResponseEntity<CommonApiResponse<DeliveryRouteReadResponse>> getDeliveryRoute(
        @PathVariable UUID deliveryId,
        @PathVariable Integer sequence,
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {
        DeliveryRouteReadResponse response = null;
        if(user.getRole().equals(Role.MASTER)) {
            response = DeliveryRouteReadResponse.from(
                deliveryRouteReadService.getDeliveryRoute(deliveryId, sequence)
            );
        }
        else if(user.getRole().equals(Role.HUB_MANAGER)) {
            response = DeliveryRouteReadResponse.from(
                deliveryRouteReadService.getHubDeliveryRoute(deliveryId, sequence, user.getUserId())
            );
        }
        else if(user.getRole().equals(Role.DELIVERY_DRIVER)) {
            response = DeliveryRouteReadResponse.from(
                deliveryRouteReadService.getDriverDeliveryRoute(deliveryId, sequence, user.getUserId())
            );
        }
        else throw new ForbiddenException(ResponseCode.FORBIDDEN);

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK, response));
    }
}
