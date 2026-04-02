package com.sparta.lucky.deliveryservice.application;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverCreateCommand;
import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverUpdateCommand;
import com.sparta.lucky.deliveryservice.common.error.exceptions.ConflictException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryDriverRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryDriverService {

    private final DeliveryDriverRepository deliveryDriverRepository;
    private final DeliveryDriverReadService deliveryDriverReadService;

    /**
     * 배송 담당자를 DB에 추가합니다.<br>
     * 존재하지 않는 User 또는 Hub의 ID로 시도하거나, 이미 존재하는 배송 담당자가 있는 경우 추가되지 않습니다.
     * @param command 생성할 배송 담당자의 정보를 담은 dto
     */
    @Transactional
    public void createDriver(DeliveryDriverCreateCommand command) {
        // TODO : add validation logic
        // Check if the User and Hub exist.
        // if not exist, throw bad request exception.

        // if user already exists as delivery driver, throw conflict exception.
        if(deliveryDriverRepository.findActiveByUserId(command.driverId()).isPresent()) {
            throw new ConflictException(ResponseCode.DRIVER_EXISTS);
        }

        // save new delivery driver.
        deliveryDriverRepository.save(DeliveryDriver.create(command));
    }

    /**
     * 배송 담당자 데이터를 삭제합니다.
     * @param driverId 삭제하려는 배송 담당자의 ID
     * @param accessId 삭제처리를 시도한 사용자의 ID
     */
    @Transactional
    public void deleteDriver(UUID driverId, UUID accessId) {
        deliveryDriverReadService.getActiveDriverOrThrow(driverId).softDelete(accessId);
    }

    /**
     * 배송 담당자 정보를 업데이트 합니다.
     * @param driverId 배송 담당자 ID(userId)
     * @param command 업데이트하는 내용
     */
    @Transactional
    public void updateDriver(UUID driverId, DeliveryDriverUpdateCommand command) {
        deliveryDriverReadService.getActiveDriverOrThrow(driverId).update(command);
    }

    /**
     * (WIP)<br>
     * {@code Role.HUB_MANAGER}가 업데이트를 시도하는 경우 사용합니다.<br>
     * 본인의 허브 소속이 아닌 배송담당자의 정보 수정을 시도하는 경우 예외를 발생시킵니다.
     * @param driverId 배송 담당자 ID(userId)
     * @param accessId 업데이트 시도하는 사용자의 ID
     * @param command 업데이트 내용
     */
    @Transactional
    public void updateDriver(UUID driverId, UUID accessId, DeliveryDriverUpdateCommand command) {
        DeliveryDriver driver = deliveryDriverReadService.getActiveDriverOrThrow(driverId);

        // TODO : add validation logic
        // driver.hubId != accessId.hubId ? throw forbidden exception

        driver.update(command);
    }
}
