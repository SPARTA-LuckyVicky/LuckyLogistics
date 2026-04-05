package com.sparta.lucky.deliveryservice.infrastructure.jpa;

import com.sparta.lucky.deliveryservice.domain.delivery.DeliveryRoute;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRouteRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID>,
    DeliveryRouteRepository {

    List<DeliveryRoute> findByDeliveryIdAndDeletedAtIsNull(UUID deliveryId);

    // Override =========================================================

    @Override
    default List<DeliveryRoute> findActiveAllByDeliveryId(UUID deliveryId) {
        return findByDeliveryIdAndDeletedAtIsNull(deliveryId);
    };
}
