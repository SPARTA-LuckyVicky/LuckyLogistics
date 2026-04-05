package com.sparta.lucky.deliveryservice.application.policy;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryReadResult;
import com.sparta.lucky.deliveryservice.common.error.exceptions.NotFoundException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryReadService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryReadResult getDelivery(UUID deliveryId) {
        return DeliveryReadResult.from(deliveryRepository.findActiveByDeliveryId(deliveryId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.DRIVER_NOT_FOUND)));
    }
}
