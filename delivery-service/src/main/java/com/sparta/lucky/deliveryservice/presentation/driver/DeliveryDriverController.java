package com.sparta.lucky.deliveryservice.presentation.driver;

import com.sparta.lucky.deliveryservice.application.DeliveryDriverReadService;
import com.sparta.lucky.deliveryservice.application.DeliveryDriverService;
import com.sparta.lucky.deliveryservice.common.code.Role;
import com.sparta.lucky.deliveryservice.common.response.CommonApiResponse;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.presentation.driver.payload.DeliveryDriverCreateRequest;
import com.sparta.lucky.deliveryservice.presentation.driver.payload.DeliveryDriverReadPageResponse;
import com.sparta.lucky.deliveryservice.presentation.driver.payload.DeliveryDriverReadResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Add Authorization
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DeliveryDriverController {

    private final DeliveryDriverService deliveryDriverService;
    private final DeliveryDriverReadService deliveryDriverReadService;

    // TODO : Need to add method to implement the logic below.
    // If user's role is HUB_MANAGER, check request.hubId and user.hubId
    // If request.hubId is different from `user.hubId`, throw forbidden exception
    // To ensure authorization, add logic that throws an exception if the user's role is not MASTER or HUB_MANAGER

    @Operation(summary = "배송 담당자 생성", description = "새로운 배송 담당자를 생성합니다.")
    @PostMapping
    public CommonApiResponse<Void> createDriver(
        @RequestBody @Valid final DeliveryDriverCreateRequest request
    ) {
        // TODO : Need to add role validation and processing method here

        deliveryDriverService.createDriver(request.toCommand());

        return CommonApiResponse.success(ResponseCode.DRIVER_CREATED);
    }

    @Operation(summary = "배송 담당자 삭제", description = "배송 담당자 데이터를 삭제합니다.")
    @DeleteMapping("/{driverId}")
    public CommonApiResponse<LocalDateTime> deleteDriver(
        @PathVariable UUID driverId
    ) {
        // TODO : Need to add role validation and processing method here

        // TODO : Replace UUID.randomUUID() to accessId
        // Authentication logic has not yet been added, random UUID is used
        deliveryDriverService.deleteDriver(driverId, UUID.randomUUID());

        return CommonApiResponse.success(ResponseCode.OK, LocalDateTime.now());
    }

    @Operation(summary = "배송 담당자 상세(단건) 조회", description = "배송 담당자를 상세 조회 합니다.")
    @GetMapping("/{driverId}")
    public ResponseEntity<CommonApiResponse<DeliveryDriverReadResponse>> getDriver(
        @PathVariable UUID driverId
    ) {
        // TODO : Need to add role validation and processing method here

        DeliveryDriverReadResponse response;

        // IF ROLE:MASTER
        response = DeliveryDriverReadResponse.fromResult(
            deliveryDriverReadService.getDriver(driverId)
        );
        // TODO: add case for ROLE:HUB_MANAGER

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK, response));
    }

    @Operation(summary = "배송 담당자 본인 조회", description = "배송 담당자 본인을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<CommonApiResponse<DeliveryDriverReadResponse>> getMe(
        @RequestHeader("X-User-Id") UUID userId,
        @RequestHeader("X-User-Role") Role role
    ) {
        if(!role.equals(Role.DELIVERY_DRIVER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(CommonApiResponse.error(ResponseCode.FORBIDDEN));
        }

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK, DeliveryDriverReadResponse.fromResult(
            deliveryDriverReadService.getDriver(userId)
        )));
    }

    @Operation(summary = "배송 담당자 목록 조회", description = "배송담당자 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<CommonApiResponse<DeliveryDriverReadPageResponse>> getDrivers(
        @PageableDefault(page = 0, size = 10) Pageable pageable,
        @RequestHeader("X-User-Id")  UUID userId,
        @RequestHeader("X-User-Role") Role role
    ) {
        // TODO : add authorization checking logic
        // if user's role is not MASTER or HUB_MANAGER, return forbidden

        DeliveryDriverReadPageResponse response = null;

        // branching by role
        if(role.equals(Role.MASTER)) {
            response = DeliveryDriverReadPageResponse.from(
                deliveryDriverReadService.getDrivers(pageable)
            );
        }
        else if (role.equals(Role.HUB_MANAGER)) {
            response = DeliveryDriverReadPageResponse.from(
                  deliveryDriverReadService.getDrivers(pageable, userId)
                );
        }

        return ResponseEntity.ok(CommonApiResponse.success(ResponseCode.OK, response));
    }
}
