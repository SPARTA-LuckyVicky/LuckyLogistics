package com.sparta.lucky.deliveryservice.presentation.driver;

import com.sparta.lucky.deliveryservice.application.DeliveryDriverService;
import com.sparta.lucky.deliveryservice.common.response.CommonApiResponse;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.presentation.driver.payload.DeliveryDriverCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "배송 담당자 생성", description = "새로운 배송 담당자를 생성합니다.\n허브 담당자의 경우 request param으로 hub_id가 필요합니다.")
    @PostMapping
    public CommonApiResponse<Void> createDriver(
        @RequestBody @Valid final DeliveryDriverCreateRequest request
    ) {
        // TODO : Need to add code to implement the logic below.
        // If user's role is HUB_MANAGER, check request.hubId and user.hubId
        // If request.hubId is different from `user.hubId`, throw forbidden exception

        deliveryDriverService.createDriver(request.toCommand());

        return CommonApiResponse.success(ResponseCode.DRIVER_CREATED);
    }
}
