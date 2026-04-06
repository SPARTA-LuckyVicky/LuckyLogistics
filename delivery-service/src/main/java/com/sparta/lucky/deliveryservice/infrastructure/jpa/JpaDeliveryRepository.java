package com.sparta.lucky.deliveryservice.infrastructure.jpa;

import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeliveryRepository extends JpaRepository<Delivery, UUID>, DeliveryRepository {

    Optional<Delivery> findByOrderIdAndDeletedAtIsNull(UUID orderId);
    Optional<Delivery> findByIdAndDeletedAtIsNull(UUID id);

    // override ====================================================

    @Override
    default Optional<Delivery> findActiveByOrderId(UUID orderId) {
        return findByOrderIdAndDeletedAtIsNull(orderId);
    };

    @Override
    default Optional<Delivery> findActiveByDeliveryId(UUID deliveryId) {
        return findByIdAndDeletedAtIsNull(deliveryId);
    };
}
