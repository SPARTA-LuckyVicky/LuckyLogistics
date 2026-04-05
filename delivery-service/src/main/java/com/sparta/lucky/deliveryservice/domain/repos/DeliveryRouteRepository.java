package com.sparta.lucky.deliveryservice.domain.repos;

import com.sparta.lucky.deliveryservice.domain.delivery.DeliveryRoute;
import java.util.List;
import java.util.UUID;

public interface DeliveryRouteRepository {

    List<DeliveryRoute> saveAll(List<DeliveryRoute> deliveryRoutes);
    List<DeliveryRoute> findActiveAllByDeliveryId(UUID deliveryId);
}
