package com.sparta.lucky.deliveryservice.domain.repos;

import com.sparta.lucky.deliveryservice.domain.delivery.DeliveryRoute;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryRouteRepository {

    Optional<DeliveryRoute> findByDeliveryIdAndSequence(UUID deliveryId, Integer sequence);
    Optional<DeliveryRoute> findByDeliveryIdAndSequenceAndHubId(UUID deliveryId, Integer sequence, UUID hubId);
    Optional<DeliveryRoute> findByDeliveryIdAndSequenceAndDriverId(UUID deliveryId, Integer sequence, UUID driverId);

    List<DeliveryRoute> findActiveAllByDeliveryId(UUID deliveryId);
    Page<DeliveryRoute> findAllByDeliveryId(UUID deliveryId, Pageable pageable);
    Page<DeliveryRoute> findAllByDeliveryIdAndHubId(UUID deliveryId, UUID hubId, Pageable pageable);
    Page<DeliveryRoute> findAllByDeliveryIdAndDriverId(UUID deliveryId, UUID driverId, Pageable pageable);
}
