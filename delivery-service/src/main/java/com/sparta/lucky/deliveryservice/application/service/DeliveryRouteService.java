package com.sparta.lucky.deliveryservice.application.service;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryRouteCreateDto;
import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.delivery.DeliveryRoute;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRouteRepository;
import com.sparta.lucky.deliveryservice.infrastructure.client.dto.HubRouteResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryRouteService {

    private final DeliveryRouteRepository deliveryRouteRepository;

    @Transactional
    public void createDeliveryRoute(HubRouteResponse hubRouteResponse, Delivery delivery, DeliveryDriver deliveryDriver) {
        List<DeliveryRoute> routeList = new ArrayList<>();
        for(int i = 0; i <= hubRouteResponse.route().size(); i++){
            DeliveryRoute route = DeliveryRoute.create(DeliveryRouteCreateDto.from(
                hubRouteResponse.route().get(i),
                delivery, deliveryDriver, i+1
            ));
            routeList.add(route);
        }
        deliveryRouteRepository.saveAll(routeList);
    }

    @Transactional
    public void deleteDeliveryRoutes(UUID deliveryId, UUID accessId) {
        deliveryRouteRepository.findActiveAllByDeliveryId(deliveryId)
            .forEach(route -> route.softDelete(accessId));
    }
}
