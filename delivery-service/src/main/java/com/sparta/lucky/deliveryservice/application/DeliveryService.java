package com.sparta.lucky.deliveryservice.application;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryCreateCommand;
import com.sparta.lucky.deliveryservice.application.dto.DeliveryCreateEventDto;
import com.sparta.lucky.deliveryservice.application.event.DeliveryCreateEvent;
import com.sparta.lucky.deliveryservice.common.error.exceptions.ConflictException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRepository;
import com.sparta.lucky.deliveryservice.infrastructure.client.CompanyClient;
import com.sparta.lucky.deliveryservice.infrastructure.client.dto.CompanyResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CompanyClient companyClient;

    /**
     * 배송 데이터를 생성합니다.
     * @param command 배송 데이터 정보
     */
    @Transactional
    public UUID createDelivery(DeliveryCreateCommand command) {

        if(deliveryRepository.findActiveByOrderId(command.orderId()).isPresent()) {
            throw new ConflictException(ResponseCode.DELIVERY_EXISTS);
        }

        // Get company's hubId and address from company-service
        CompanyResponse res = companyClient.getCompanyInfo(command.companyId());
        Delivery delivery = Delivery.create(command, res.hubId(), res.address());
        deliveryRepository.save(delivery);

        // event based follow-up processing
        eventPublisher.publishEvent(new DeliveryCreateEvent(DeliveryCreateEventDto.from(delivery)));

        return delivery.getId();
    }
}
