package com.sparta.lucky.deliveryservice.application;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryRouteReadResult;
import com.sparta.lucky.deliveryservice.common.error.exceptions.NotFoundException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.domain.delivery.DeliveryRoute;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRouteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryRouteReadService {

    private final DeliveryRouteRepository deliveryRouteRepository;

    public List<DeliveryRouteReadResult> getDeliveryRoutes(UUID deliveryId) {
        List<DeliveryRoute> routes = deliveryRouteRepository.findActiveAllByDeliveryId(deliveryId);
        if (routes.isEmpty()) {
            throw new NotFoundException(ResponseCode.ROUTE_NOT_FOUND);
        }

        List<DeliveryRouteReadResult> result = new ArrayList<>();
        routes.stream().map(DeliveryRouteReadResult::from).forEach(result::add);
        return result;
    }
}
