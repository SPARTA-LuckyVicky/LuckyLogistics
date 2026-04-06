package com.sparta.lucky.deliveryservice.application.service;

import com.sparta.lucky.deliveryservice.common.error.exceptions.NotFoundException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryStatus;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryFailureService {

    private final DeliveryRepository deliveryRepository;

    // Note: Separate this method? or write in DeliveryService?
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findActiveByDeliveryId(deliveryId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.DELIVERY_NOT_FOUND));

        delivery.updateStatus(DeliveryStatus.FAILED);
    }
}
