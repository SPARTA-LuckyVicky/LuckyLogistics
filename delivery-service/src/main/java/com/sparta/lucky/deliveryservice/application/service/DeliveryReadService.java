package com.sparta.lucky.deliveryservice.application.service;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryReadResult;
import com.sparta.lucky.deliveryservice.common.error.exceptions.ForbiddenException;
import com.sparta.lucky.deliveryservice.common.error.exceptions.NotFoundException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRepository;
import com.sparta.lucky.deliveryservice.infrastructure.client.UserClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryReadService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryDriverReadService deliveryDriverReadService;
    private final UserClient userClient;

    /**
     * 배송 데이터를 조회하고 반환합니다.
     * @param deliveryId
     * @return {@code DeliveryReadResult}
     */
    public DeliveryReadResult getDelivery(UUID deliveryId) {
        return DeliveryReadResult.from(deliveryRepository.findActiveByDeliveryId(deliveryId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.DELIVERY_NOT_FOUND)));
    }

    /**
     * {@code Role.HUB_MANAGER} 인 사용자가 배송 데이터를 조회하는 경우 사용합니다.
     * @param deliveryId
     * @param accessUserId
     * @return {@code DeliveryDriverReadResult}
     */
    public DeliveryReadResult getHubDelivery(UUID deliveryId, UUID accessUserId) {
        UUID hubId = userClient.getUser(accessUserId).hubId();
        return DeliveryReadResult.from(deliveryRepository.findActiveByIdAndHubId(deliveryId, hubId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.DELIVERY_NOT_FOUND)));
    }

    /**
     * {@code Role.DELIVERY_DRIVER} 인 사용자가 배송 데이터를 조회하는 경우 사용합니다.<br>
     * 허브 담당 배송자는 조회할 수 없습니다.
     * @param deliveryId
     * @param accessUserId
     * @return {@code DeliveryDriverReadResult}
     */
    public DeliveryReadResult getDriverDelivery(UUID deliveryId, UUID accessUserId) {
        DeliveryDriver driver = deliveryDriverReadService.getActiveDriverOrThrow(accessUserId);
        if(driver.getType().equals(DriverType.HUB)) throw new ForbiddenException(ResponseCode.DELIVERY_READ_NOT_ALLOWED);
        return DeliveryReadResult.from(deliveryRepository.findActiveByIdAndDriver(deliveryId, driver)
            .orElseThrow(() -> new NotFoundException(ResponseCode.DELIVERY_NOT_FOUND)));
    }

    /**
     * {@code Role.COMPANY_MANGER} 인 사용자가 배송 데이터를 조회하는 경우 사용합니다.
     * @param deliveryId
     * @param accessUserId
     * @return {@code DeliveryDriverReadResult}
     */
    public DeliveryReadResult getUserDelivery(UUID deliveryId, UUID accessUserId) {
        String slackId = userClient.getUser(accessUserId).slackId();
        return DeliveryReadResult.from(deliveryRepository.findActiveByIdAndSlackId(deliveryId, slackId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.DELIVERY_NOT_FOUND)));
    }

    // Page query
    /**
     * 배송 목록을 조회하고 Page로 반환합니다.
     * @param pageable
     * @return {@code Page<DeliveryReadResult>}
     */
    public Page<DeliveryReadResult> getAllDeliveries(Pageable pageable) {
        return deliveryRepository.findAllActive(pageable).map(DeliveryReadResult::from);
    }

    /**
     * {@code Role.HUB_MANAGER}인 사용자가 배송목록을 조회하는 경우 사용합니다.
     * @param pageable
     * @param accessUserId
     * @return {@code Page<DeliveryReadResult>}
     */
    public Page<DeliveryReadResult> getHubDeliveries(Pageable pageable, UUID accessUserId) {
        UUID hubId = userClient.getUser(accessUserId).hubId();
        return deliveryRepository.findAllActiveByHub(hubId, pageable).map(DeliveryReadResult::from);
    }

    /**
     * {@code Role.DELIVERY_DRIVER}인 사용자가 배송목록을 조회하는 경우 사용합니다.<br>
     * 업체 배송 담당자만 조회가 가능합니다.
     * @param pageable
     * @param accessUserId
     * @return {@code Page<DeliveryReadResult>}
     */
    public Page<DeliveryReadResult> getDriverDeliveries(Pageable pageable, UUID accessUserId) {
        DeliveryDriver driver = deliveryDriverReadService.getActiveDriverOrThrow(accessUserId);

        if(driver.getType().equals(DriverType.HUB)) throw new ForbiddenException(ResponseCode.DELIVERY_READ_NOT_ALLOWED);

        return deliveryRepository.findAllActiveByDriver(driver, pageable).map(DeliveryReadResult::from);
    }
}
