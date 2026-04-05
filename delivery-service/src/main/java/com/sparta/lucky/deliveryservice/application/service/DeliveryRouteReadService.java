package com.sparta.lucky.deliveryservice.application.service;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryRouteReadResult;
import com.sparta.lucky.deliveryservice.common.error.exceptions.ForbiddenException;
import com.sparta.lucky.deliveryservice.common.error.exceptions.NotFoundException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.delivery.DeliveryRoute;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRouteRepository;
import com.sparta.lucky.deliveryservice.infrastructure.client.UserClient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryRouteReadService {

    private final DeliveryRouteRepository deliveryRouteRepository;
    private final DeliveryDriverReadService deliveryDriverReadService;
    private final UserClient userClient;

    public DeliveryRouteReadResult getDeliveryRoute(UUID deliveryId, Integer sequence) {
        return DeliveryRouteReadResult.from(
            deliveryRouteRepository.findByDeliveryIdAndSequence(deliveryId, sequence)
                .orElseThrow(() -> new NotFoundException(ResponseCode.ROUTE_NOT_FOUND)));
    }

    public DeliveryRouteReadResult getHubDeliveryRoute(UUID deliveryId, Integer sequence, UUID accessUserId) {
        UUID hubId = userClient.getUser(accessUserId).hubId();
        return DeliveryRouteReadResult.from(
            deliveryRouteRepository.findByDeliveryIdAndSequenceAndHubId(deliveryId, sequence, hubId)
                .orElseThrow(() -> new NotFoundException(ResponseCode.ROUTE_NOT_FOUND))
        );
    }

    public DeliveryRouteReadResult getDriverDeliveryRoute(UUID deliveryId, Integer sequence, UUID accessUserId) {
        if(deliveryDriverReadService.getDriver(accessUserId).type().equals(DriverType.COMPANY)) {
            throw new ForbiddenException(ResponseCode.ROUTE_READ_NOT_ALLOWED);
        }
        return DeliveryRouteReadResult.from(
            deliveryRouteRepository.findByDeliveryIdAndSequenceAndDriverId(deliveryId, sequence, accessUserId)
                .orElseThrow(()  -> new NotFoundException(ResponseCode.ROUTE_NOT_FOUND))
        );
    }

    public List<DeliveryRouteReadResult> getDeliveryRoutes(UUID deliveryId) {
        List<DeliveryRoute> routes = deliveryRouteRepository.findActiveAllByDeliveryId(deliveryId);
        if (routes.isEmpty()) {
            throw new NotFoundException(ResponseCode.ROUTE_NOT_FOUND);
        }

        List<DeliveryRouteReadResult> result = new ArrayList<>();
        routes.stream().map(DeliveryRouteReadResult::from).forEach(result::add);
        return result;
    }

    // Page query
    public Page<DeliveryRouteReadResult> getDeliveryRoutes(Pageable pageable, UUID deliveryId) {
        return deliveryRouteRepository.findAllByDeliveryId(deliveryId, pageable).map(DeliveryRouteReadResult::from);
    }

    public Page<DeliveryRouteReadResult> getHubDeliveryRoutes(Pageable pageable, UUID deliveryId, UUID accessUserId) {
        UUID hubId = userClient.getUser(accessUserId).hubId();
        return deliveryRouteRepository.findAllByDeliveryIdAndHubId(deliveryId, hubId, pageable)
            .map(DeliveryRouteReadResult::from);
    }

    public Page<DeliveryRouteReadResult> getDriverDeliveryRoutes(Pageable pageable, UUID deliveryId, UUID accessUserId) {
        if(deliveryDriverReadService.getDriver(accessUserId).type().equals(DriverType.COMPANY)) {
            throw new ForbiddenException(ResponseCode.ROUTE_READ_NOT_ALLOWED);
        }
        return deliveryRouteRepository.findAllByDeliveryIdAndDriverId(deliveryId, accessUserId, pageable)
            .map(DeliveryRouteReadResult::from);
    }
}
