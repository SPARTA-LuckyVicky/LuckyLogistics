package com.sparta.lucky.deliveryservice.application;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverReadResult;
import com.sparta.lucky.deliveryservice.common.error.exceptions.NotFoundException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryDriverRepository;
import com.sparta.lucky.deliveryservice.presentation.driver.payload.DeliveryDriverReadResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryDriverReadService {

    private final DeliveryDriverRepository deliveryDriverRepository;

    /**
     * 배송 담당자를 조회합니다.
     * @param driverId 조회하려는 배송 담당자의 ID(userId)
     * @return {@link DeliveryDriverReadResult}
     */
    public DeliveryDriverReadResult getDriver(UUID driverId) {
        DeliveryDriver driver = getActiveDriverOrThrow(driverId);
        return DeliveryDriverReadResult.from(driver);
    }

    /**
     * (WIP) {@code ROLE:HUB_MANAGER}인 사용자가 배송담당자를 조회하는 경우 사용됩니다. <br>
     * 본인의 허브에 소속된 배송담당자를 조회하는것이 아닌 경우, 예외를 발생시킵니다.
     * @param driverId 조회하려는 배송 담당자의 ID(userId)
     * @param accessId 조회를 시도한 사용자의 ID
     * @return {@link DeliveryDriverReadResult}
     */
    public DeliveryDriverReadResult getDriver(UUID driverId, UUID accessId) {
        DeliveryDriver driver = getActiveDriverOrThrow(driverId);

        // TODO : add logic below
        // 1. get the hubId to which the user attempting the query belongs.
        // 2-a. if driver.hubId and accessor.hubId are the same, return result
        // 2-b. else, throw forbidden exception

        return DeliveryDriverReadResult.from(driver);
    }

    /**
     * 배송 담당자 목록을 조회하고 Page로 반환합니다.
     * @param pageable
     * @return {@code Page<DeliveryDriverReadResult>}
     */
    public Page<DeliveryDriverReadResult> getDrivers(Pageable pageable) {
        return deliveryDriverRepository.findAllActive(pageable).map(DeliveryDriverReadResult::from);
    }

    /**
     * (WIP) {@code ROLE:HUB_MANAGER}인 사용자가 배송담당자 목록을 조회하는 경우 사용됩니다.
     * @param pageable
     * @param accessId 조회를 시도한 사용자의 ID
     * @return
     */
    public Page<DeliveryDriverReadResult> getDrivers(Pageable pageable, UUID accessId) {
        // TODO : add logic below
        // 1. get the hubId to which the user attempting the query belongs.
        // 2. query the list of delivery drivers using the hubId
        return null;
    }

    // Internal Query Methods =========================================================
    public DeliveryDriver getActiveDriverOrThrow(UUID driverId) {
        return deliveryDriverRepository.findActiveByUserId(driverId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.DRIVER_NOT_FOUND));
    }
}
