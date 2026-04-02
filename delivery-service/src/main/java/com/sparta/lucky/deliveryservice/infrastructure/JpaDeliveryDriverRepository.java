package com.sparta.lucky.deliveryservice.infrastructure;

import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryDriverRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeliveryDriverRepository
    extends JpaRepository<DeliveryDriver, UUID>, DeliveryDriverRepository {

    Optional<DeliveryDriver> findByUserIdAndDeletedAtIsNull(UUID id);


    // override ====================================================
    @Override
    default Optional<DeliveryDriver> findActiveByUserId(UUID id) {
        return findByUserIdAndDeletedAtIsNull(id);
    }
}
