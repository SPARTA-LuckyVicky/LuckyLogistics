package com.sparta.lucky.deliveryservice.presentation.driver;

import com.sparta.lucky.deliveryservice.application.DeliveryDriverService;
import com.sparta.lucky.deliveryservice.common.response.CommonApiResponse;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.presentation.driver.payload.DeliveryDriverCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: Add Authorization
@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DeliveryDriverController {

    private final DeliveryDriverService deliveryDriverService;

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
}
