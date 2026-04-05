package com.sparta.lucky.deliveryservice.infrastructure.jpa;

import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeliveryRepository extends JpaRepository<Delivery, UUID>, DeliveryRepository {

    Optional<Delivery> findByOrderIdAndDeletedAtIsNull(UUID orderId);
    Optional<Delivery> findByIdAndDeletedAtIsNull(UUID id);

    Page<Delivery> findAllByDeletedAtIsNull(Pageable pageable);
    Page<Delivery> findAllByDeliveryDriverAndDeletedAtIsNull(DeliveryDriver driver, Pageable pageable);

    // override ====================================================

    @Override
    default Optional<Delivery> findActiveByOrderId(UUID orderId) {
        return findByOrderIdAndDeletedAtIsNull(orderId);
    };

    @Override
    default Optional<Delivery> findActiveByDeliveryId(UUID deliveryId) {
        return findByIdAndDeletedAtIsNull(deliveryId);
    };

    // Page query
    @Override
    default Page<Delivery> findAllActive(Pageable pageable) {
        return findAllByDeletedAtIsNull(pageable);
    }

    @Override
    @Query("""
        select d
        from Delivery d
        where d.deletedAt is null
            and d.originHub = :hubId or d.destinationHub = :hubId
        """)
    Page<Delivery> findAllActiveByHub(@Param("hubId") UUID hubId, Pageable pageable);

    @Override
    default Page<Delivery> findAllActiveByDriver(DeliveryDriver driver, Pageable pageable) {
        return findAllByDeliveryDriverAndDeletedAtIsNull(driver, pageable);
    }
}
