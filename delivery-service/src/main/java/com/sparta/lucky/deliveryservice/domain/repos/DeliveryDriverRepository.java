package com.sparta.lucky.deliveryservice.domain.repos;

import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryDriverRepository {

    Optional<DeliveryDriver> findActiveById(UUID id);
    DeliveryDriver save(DeliveryDriver deliveryDriver);
}
