package com.sparta.lucky.deliveryservice.application;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverCreateCommand;
import com.sparta.lucky.deliveryservice.common.error.exceptions.ConflictException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryDriverRepository;
import com.sparta.lucky.deliveryservice.infrastructure.JpaDeliveryDriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryDriverService {

    private final DeliveryDriverRepository deliveryDriverRepository;

    /**
     * 배송 담당자를 DB에 추가합니다.<br>
     * 존재하지 않는 User 또는 Hub의 ID로 시도하거나, 이미 존재하는 배송 담당자가 있는 경우 추가되지 않습니다.
     * @param command 생성할 배송 담당자의 정보를 담은 dto
     */
    public void createDriver(DeliveryDriverCreateCommand command) {
        // TODO : add validation logic
        // Check if the User and Hub exist.
        // if not exist, throw bad request exception.

        // if user already exists as delivery driver, throw conflict exception.
        if(deliveryDriverRepository.findActiveById(command.driverId()).isPresent()) {
            throw new ConflictException(ResponseCode.DRIVER_EXISTS);
        }

        // save new delivery driver.
        deliveryDriverRepository.save(DeliveryDriver.create(command));
    }
}
