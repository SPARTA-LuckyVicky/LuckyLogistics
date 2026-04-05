package com.sparta.lucky.deliveryservice.domain.repos;

import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryRepository {

    Optional<Delivery> findActiveByOrderId(UUID orderId);
    Optional<Delivery> findActiveByDeliveryId(UUID deliveryId);
    Delivery save(Delivery delivery);

    Page<Delivery> findAllActive(Pageable pageable);
    Page<Delivery> findAllActiveByHub(UUID hubId, Pageable pageable);
    Page<Delivery> findAllActiveByDriver(DeliveryDriver driver, Pageable pageable);
}
