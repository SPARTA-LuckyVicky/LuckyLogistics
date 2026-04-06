package com.sparta.lucky.deliveryservice.presentation.driver;

import com.sparta.lucky.deliveryservice.application.DeliveryDriverReadService;
import com.sparta.lucky.deliveryservice.application.DeliveryDriverService;
import com.sparta.lucky.deliveryservice.common.code.Role;
import com.sparta.lucky.deliveryservice.common.response.CommonApiResponse;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.common.security.auth.ExternalUserPrincipal;
import com.sparta.lucky.deliveryservice.presentation.driver.payload.DeliveryDriverCreateRequest;
import com.sparta.lucky.deliveryservice.presentation.driver.payload.DeliveryDriverReadPageResponse;
import com.sparta.lucky.deliveryservice.presentation.driver.payload.DeliveryDriverReadResponse;
import com.sparta.lucky.deliveryservice.presentation.driver.payload.DeliveryDriverUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DeliveryDriverController {

    private final DeliveryDriverService deliveryDriverService;
    private final DeliveryDriverReadService deliveryDriverReadService;

    @Operation(summary = "배송 담당자 생성", description = "새로운 배송 담당자를 생성합니다.")
    @PostMapping
    public ResponseEntity<CommonApiResponse<Void>> createDriver(
        @RequestBody @Valid final DeliveryDriverCreateRequest request,
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {
        if(user.getRole().equals(Role.MASTER)) {
            deliveryDriverService.createDriver(request.toCommand());
        } else if (user.getRole().equals(Role.HUB_MANAGER)) {
            deliveryDriverService.createDriver(request.toCommand(), user.getUserId());
        }

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.DRIVER_CREATED));
    }

    @Operation(summary = "배송 담당자 삭제", description = "배송 담당자 데이터를 삭제합니다.")
    @DeleteMapping("/{driverId}")
    public ResponseEntity<CommonApiResponse<LocalDateTime>> deleteDriver(
        @PathVariable UUID driverId,
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {
        deliveryDriverService.deleteDriver(driverId, user.getUserId(), user.getRole());

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK, LocalDateTime.now()));
    }

    @Operation(summary = "배송 담당자 상세(단건) 조회", description = "배송 담당자를 상세 조회 합니다.")
    @GetMapping("/{driverId}")
    public ResponseEntity<CommonApiResponse<DeliveryDriverReadResponse>> getDriver(
        @PathVariable UUID driverId,
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {
        DeliveryDriverReadResponse response = null;

        // Branching by Role
        if(user.getRole().equals(Role.MASTER)) {
            response = DeliveryDriverReadResponse.fromResult(
                deliveryDriverReadService.getDriver(driverId)
            );
        } else if (user.getRole().equals(Role.HUB_MANAGER)) {
            response = DeliveryDriverReadResponse.fromResult(
                deliveryDriverReadService.getDriver(driverId, user.getUserId())
            );
        }

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK, response));
    }

    @Operation(summary = "배송 담당자 본인 조회", description = "배송 담당자 본인을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<CommonApiResponse<DeliveryDriverReadResponse>> getMe(
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {
        if(!user.getRole().equals(Role.DELIVERY_DRIVER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(CommonApiResponse.error(ResponseCode.FORBIDDEN));
        }

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK, DeliveryDriverReadResponse.fromResult(
            deliveryDriverReadService.getDriver(user.getUserId())
        )));
    }

    @Operation(summary = "배송 담당자 목록 조회", description = "배송담당자 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonApiResponse<DeliveryDriverReadPageResponse>> getDrivers(
        @PageableDefault(page = 0, size = 10) Pageable pageable,
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {
        DeliveryDriverReadPageResponse response = null;

        // branching by role
        if(user.getRole().equals(Role.MASTER)) {
            response = DeliveryDriverReadPageResponse.from(
                deliveryDriverReadService.getDrivers(pageable)
            );
        }
        else if (user.getRole().equals(Role.HUB_MANAGER)) {
            response = DeliveryDriverReadPageResponse.from(
                  deliveryDriverReadService.getDrivers(pageable, user.getUserId())
                );
        }

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK, response));
    }

    @Operation(summary = "배송 담당자 정보 업데이트", description = "배송담당자의 정보를 업데이트합니다.")
    @PatchMapping("/{driverId}")
    public ResponseEntity<CommonApiResponse<?>> updateDriver(
        @PathVariable UUID driverId,
        @RequestBody final DeliveryDriverUpdateRequest request,
        @AuthenticationPrincipal ExternalUserPrincipal user
    ) {

        // Branching by Role
        if(user.getRole().equals(Role.MASTER)) {
            deliveryDriverService.updateDriver(driverId, request.toCommand());
        } else if(user.getRole().equals(Role.HUB_MANAGER)) {
            deliveryDriverService.updateDriver(driverId, user.getUserId(), request.toCommand());
        }


        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK));
    }
}
