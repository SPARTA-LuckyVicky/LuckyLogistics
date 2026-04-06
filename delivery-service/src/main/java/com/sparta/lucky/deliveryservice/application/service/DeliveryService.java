package com.sparta.lucky.deliveryservice.application.service;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryCreateCommand;
import com.sparta.lucky.deliveryservice.application.dto.DeliveryCreateEventDto;
import com.sparta.lucky.deliveryservice.application.event.DeliveryCreateEvent;
import com.sparta.lucky.deliveryservice.application.policy.HubAccessValidator;
import com.sparta.lucky.deliveryservice.common.code.Role;
import com.sparta.lucky.deliveryservice.common.error.exceptions.BusinessException;
import com.sparta.lucky.deliveryservice.common.error.exceptions.ConflictException;
import com.sparta.lucky.deliveryservice.common.error.exceptions.NotFoundException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryStatus;
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
    private final HubAccessValidator hubAccessValidator;
    private final DeliveryRouteService deliveryRouteService;
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

    /**
     * 배송 데이터를 삭제합니다.<br>
     * 배송 데이터가 삭제되는 경우, 배송 경로데이터도 함께 삭제됩니다.<br>
     * 허브 담당자의 경우, 출발 허브의 담당자만 데이터를 삭제할 수 있습니다.<br>
     * 또한, 이미 배송중인 주문은 삭제할 수 없습니다.
     * @param deliveryId 삭제하려는 배송 데이터의 ID
     */
    @Transactional
    public void deleteDelivery(UUID deliveryId, UUID accessId, Role role) {
        Delivery delivery = deliveryRepository.findActiveByDeliveryId(deliveryId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.DELIVERY_NOT_FOUND));

        // check is delivery already in transit
        if(delivery.getStatus().equals(DeliveryStatus.PENDING) || delivery.getStatus().equals(DeliveryStatus.WAITING)) {
            throw new BusinessException(ResponseCode.DELIVERY_DELETE_NOT_ALLOWED);
        }

        // check permissions
        if(!role.equals(Role.MASTER)) {
            hubAccessValidator.validateSameHubOrThrow(accessId, delivery.getOriginHub());
        }

        // delete delivery routes before delete delivery
        if(!delivery.getOriginHub().equals(delivery.getDestinationHub())) {
            deliveryRouteService.deleteDeliveryRoutes(deliveryId, accessId);
        }

        delivery.softDelete(accessId);
    }
}
