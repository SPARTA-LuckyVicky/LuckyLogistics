package com.sparta.lucky.deliveryservice.infrastructure;

import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryDriverRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeliveryDriverRepository
    extends JpaRepository<DeliveryDriver, UUID>, DeliveryDriverRepository {

    Optional<DeliveryDriver> findByUserIdAndDeletedAtIsNull(UUID id);
    Page<DeliveryDriver> findAllByDeletedAtIsNull(Pageable pageable);
    Page<DeliveryDriver> findAllByHubIdAndDeletedAtIsNull(UUID hubId, Pageable pageable);


    // override ====================================================
    @Override
    default Optional<DeliveryDriver> findActiveByUserId(UUID id) {
        return findByUserIdAndDeletedAtIsNull(id);
    }

    @Override
    default Page<DeliveryDriver> findAllActive(Pageable pageable) {
        return findAllByDeletedAtIsNull(pageable);
    }

    @Override
    default Page<DeliveryDriver> findAllActiveByHubId(UUID hubId, Pageable pageable) {
        return findAllByHubIdAndDeletedAtIsNull(hubId, pageable);
    };
}
