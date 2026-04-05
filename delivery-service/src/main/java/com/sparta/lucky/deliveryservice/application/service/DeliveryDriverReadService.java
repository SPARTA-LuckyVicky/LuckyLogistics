package com.sparta.lucky.deliveryservice.application.service;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverReadResult;
import com.sparta.lucky.deliveryservice.common.error.exceptions.ForbiddenException;
import com.sparta.lucky.deliveryservice.common.error.exceptions.NotFoundException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverStatus;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryDriverRepository;
import com.sparta.lucky.deliveryservice.infrastructure.client.UserClient;
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
    private final UserClient userClient;

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

        validateSameHubOrThrow(accessId, driver.getHubId());

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
        UUID hubId = userClient.getUserHubId(accessId).hubId();
        return deliveryDriverRepository.findAllActiveByHubId(hubId, pageable).map(DeliveryDriverReadResult::from);
    }

    // Internal Query Methods =========================================================
    public DeliveryDriver getActiveDriverOrThrow(UUID driverId) {
        return deliveryDriverRepository.findActiveByUserId(driverId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.DRIVER_NOT_FOUND));
    }

    /**
     * 배송가능한 업체 배송 담당자의 id를 반환합니다.
     * @param hubId 소속 hubId
     * @return {@code UUID} driverId
     */
    public DeliveryDriver getOneCompanyDriver(UUID hubId) {
        return deliveryDriverRepository.findFirstActiveByHubId(hubId, DriverStatus.IDLE, DriverType.COMPANY)
            .orElseThrow(() -> new NotFoundException(ResponseCode.NO_DRIVER_AVAILABLE));
    }

    /**
     * 배송 가능한 허브 배송담당자의 ID를 반환합니다.
     * @return {@code UUID} driverId
     */
    public DeliveryDriver getOneHubDriver() {
        return deliveryDriverRepository.findFirstActiveByStatusAndType(DriverStatus.IDLE, DriverType.HUB)
            .orElseThrow(() -> new NotFoundException(ResponseCode.NO_DRIVER_AVAILABLE));
    }

    // Validator =======================================================================
    private void validateSameHubOrThrow(UUID accessId, UUID hubId) {
        UUID userHubId = userClient.getUserHubId(accessId).hubId();
        if (!hubId.equals(userHubId)) {
            throw new ForbiddenException(ResponseCode.FORBIDDEN);
        }
    }
}
