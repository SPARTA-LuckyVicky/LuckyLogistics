package com.sparta.lucky.deliveryservice.infrastructure.jpa;

import com.sparta.lucky.deliveryservice.domain.driver.DeliveryDriver;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverStatus;
import com.sparta.lucky.deliveryservice.domain.driver.code.DriverType;
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
    Optional<DeliveryDriver> findFirstByHubIdAndStatusAndTypeAndDeletedAtIsNullOrderByAssignmentOrderAscIdAsc(
        UUID hubId, DriverStatus status, DriverType type);
    Optional<DeliveryDriver> findFirstByStatusAndTypeAndDeletedAtIsNullOrderByAssignmentOrderAscIdAsc(
        DriverStatus status, DriverType type
    );


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

    @Override
    default Optional<DeliveryDriver> findFirstActiveByHubId(UUID hubId, DriverStatus status, DriverType type) {
        return findFirstByHubIdAndStatusAndTypeAndDeletedAtIsNullOrderByAssignmentOrderAscIdAsc(hubId, status, type);
    };

    @Override
    default Optional<DeliveryDriver> findFirstActiveByStatusAndType(DriverStatus status, DriverType type) {
        return findFirstByStatusAndTypeAndDeletedAtIsNullOrderByAssignmentOrderAscIdAsc(status, type);
    }
}
