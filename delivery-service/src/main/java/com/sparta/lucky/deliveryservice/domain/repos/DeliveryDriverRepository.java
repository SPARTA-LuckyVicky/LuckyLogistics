package com.sparta.lucky.deliveryservice.domain.repos;

import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryDriverRepository {

    Optional<DeliveryDriver> findActiveByUserId(UUID id);
    DeliveryDriver save(DeliveryDriver deliveryDriver);
    Page<DeliveryDriver> findAllActive(Pageable pageable);
}
