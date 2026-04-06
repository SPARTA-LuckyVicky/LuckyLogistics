package com.sparta.lucky.deliveryservice.domain.repos;

import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverStatus;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryDriverRepository {

    Optional<DeliveryDriver> findActiveByUserId(UUID id);
    DeliveryDriver save(DeliveryDriver deliveryDriver);
    Page<DeliveryDriver> findAllActive(Pageable pageable);
    Page<DeliveryDriver> findAllActiveByHubId(UUID hubId, Pageable pageable);
    Optional<DeliveryDriver> findFirstActiveByHubId(UUID hubId,  DriverStatus status, DriverType type);
    Optional<DeliveryDriver> findFirstActiveByStatusAndType(DriverStatus status, DriverType type);
    Integer findMaxAssignmentOrder(DriverType type);
    Integer findMaxAssignmentOrder(UUID hubId, DriverType type);
}
