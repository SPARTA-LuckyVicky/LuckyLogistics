package com.sparta.lucky.deliveryservice.application;

import com.sparta.lucky.deliveryservice.application.event.DeliveryProcessedEvent;
import com.sparta.lucky.deliveryservice.common.error.exceptions.CommonException;
import com.sparta.lucky.deliveryservice.common.error.exceptions.ConflictException;
import com.sparta.lucky.deliveryservice.common.error.exceptions.NotFoundException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.delivery.code.DeliveryStatus;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryDriverRepository;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRepository;
import com.sparta.lucky.deliveryservice.infrastructure.client.HubRouteClient;
import com.sparta.lucky.deliveryservice.infrastructure.client.dto.HubRouteResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryProcessingService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryDriverRepository deliveryDriverRepository;
    private final DeliveryDriverReadService deliveryDriverReadService;
    private final HubRouteClient  hubRouteClient;
    private final DeliveryRouteService  deliveryRouteService;
    private final DeliveryFailureService deliveryFailureService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 배송 생성시, 기본 배송 정보 생성후, 배송 담당자 배정, 배송 경로 저장 등의 후처리 작업을 처리하는 오케스트레이터
     * @param deliveryId 처리해야하는 배송 데이터의 id
     */
    @Transactional
    public void process(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findActiveByDeliveryId(deliveryId)
            .orElseThrow(() -> new NotFoundException(ResponseCode.DELIVERY_NOT_FOUND));

        if(!delivery.getStatus().equals(DeliveryStatus.PENDING)) {
            throw new ConflictException(ResponseCode.DELIVERY_ALREADY_PROCESSED);
        }

        try {
            // Note: Would it be better to implement a company driver to be assigned when the delivery arrives at the destination hub?
            // 1-a. assign company delivery driver to delivery
            DeliveryDriver companyDriver = deliveryDriverReadService.getOneCompanyDriver(delivery.getDestinationHub());
            delivery.updateDriver(companyDriver);
            // 1-b. get max sequence value and update assigned company driver's sequence value
            int companyMaxOrder = deliveryDriverRepository.findMaxAssignmentOrder(delivery.getDestinationHub(), DriverType.COMPANY);
            companyDriver.updateOrder(companyMaxOrder+1);

            // 2. If origin hub and destination hub are different, create delivery route
            if(!delivery.getOriginHub().equals(delivery.getDestinationHub())) {
                // 2-a. request route to hubRoute-service
                HubRouteResponse routeResponse = hubRouteClient.getHubRoute(delivery.getOriginHub(), delivery.getDestinationHub());

                // 2-b. check is route null
                if(routeResponse.route() == null || routeResponse.route().isEmpty()) {
                    throw new CommonException(ResponseCode.ROUTE_RESPONSE_NULL);
                }

                // 2-c. get id of available delivery driver and save deliveryRoute
                DeliveryDriver hubDriver = deliveryDriverReadService.getOneHubDriver();
                deliveryRouteService.createDeliveryRoute(routeResponse, delivery, hubDriver);

                // 2-d. get max sequence value and update assigned hub driver's sequence value
                int hubMaxOrder = deliveryDriverRepository.findMaxAssignmentOrder(DriverType.HUB);
                hubDriver.updateOrder(hubMaxOrder+1);
            }

            // 3. change status of delivery to WAITING
            delivery.updateStatus(DeliveryStatus.WAITING);

            // 4. send request to notification-service
            eventPublisher.publishEvent(new DeliveryProcessedEvent(deliveryId));
        } catch (Exception e) {
            log.error("[ERROR] DeliveryProcessingService :: processing failed. deliveryId={}", deliveryId, e);

            try {
                deliveryFailureService.markFailed(deliveryId);
            } catch (Exception markFailedEx) {
                log.error("[ERROR] Failed to mark delivery as FAILED. deliveryId={}", deliveryId, markFailedEx);
            }

            throw e;
        }
    }
}
