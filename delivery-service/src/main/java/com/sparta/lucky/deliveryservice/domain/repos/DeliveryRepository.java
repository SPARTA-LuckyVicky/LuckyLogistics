package com.sparta.lucky.deliveryservice.domain.repos;

import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {

    Optional<Delivery> findActiveByOrderId(UUID orderId);
    Optional<Delivery> findActiveByDeliveryId(UUID deliveryId);
    Delivery save(Delivery delivery);

}
