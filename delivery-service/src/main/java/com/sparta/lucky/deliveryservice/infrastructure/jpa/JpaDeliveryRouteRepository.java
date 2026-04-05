package com.sparta.lucky.deliveryservice.infrastructure.jpa;

import com.sparta.lucky.deliveryservice.domain.delivery.Delivery;
import com.sparta.lucky.deliveryservice.domain.delivery.DeliveryRoute;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRouteRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID>,
    DeliveryRouteRepository {

    Optional<DeliveryRoute> findByDelivery_IdAndSequenceAndDeletedAtIsNull(UUID deliveryId, Integer sequence);
    Optional<DeliveryRoute> findByDelivery_IdAndSequenceAndDeliveryDriver_IdAndDeletedAtIsNull(UUID deliveryId, Integer sequence, UUID driverId);

    List<DeliveryRoute> findByDelivery_IdAndDeletedAtIsNull(UUID deliveryId);
    Page<DeliveryRoute> findByDelivery_IdAndDeletedAtIsNull(UUID deliveryId, Pageable pageable);
    Page<DeliveryRoute> findByDelivery_IdAndDeliveryDriver_IdAndDeletedAtIsNull(UUID deliveryId, UUID deliveryDriverId, Pageable pageable);

    // Override =========================================================

    @Override
    default List<DeliveryRoute> findActiveAllByDeliveryId(UUID deliveryId) {
        return findByDelivery_IdAndDeletedAtIsNull(deliveryId);
    };

    @Override
    default Page<DeliveryRoute> findAllByDeliveryId(UUID deliveryId, Pageable pageable) {
        return findByDelivery_IdAndDeletedAtIsNull(deliveryId, pageable);
    }

    @Override
    @Query("""
        select dr
        from DeliveryRoute dr
        where dr.deletedAt is null
            and dr.delivery.id = :deliveryId
            and (dr.fromHubId = :hubId or dr.toHubId = :hubId)
        """)
    Page<DeliveryRoute> findAllByDeliveryIdAndHubId(UUID deliveryId, UUID hubId, Pageable pageable);

    @Override
    default Page<DeliveryRoute> findAllByDeliveryIdAndDriverId(UUID deliveryId, UUID driverId,
        Pageable pageable) {
        return findByDelivery_IdAndDeliveryDriver_IdAndDeletedAtIsNull(deliveryId, driverId, pageable);
    }

    @Override
    default Optional<DeliveryRoute> findByDeliveryIdAndSequence(UUID deliveryId, Integer sequence) {
        return findByDelivery_IdAndSequenceAndDeletedAtIsNull(deliveryId, sequence);
    }

    @Override
    @Query("""
      select dr
      from DeliveryRoute dr
      where dr.deletedAt is null
        and dr.delivery.id = :deliveryId
        and dr.sequence = :sequence
        and (dr.fromHubId = :hubId or dr.toHubId = :hubId)
      """)
    Optional<DeliveryRoute> findByDeliveryIdAndSequenceAndHubId(UUID deliveryId, Integer sequence,
        UUID hubId);

    @Override
    default Optional<DeliveryRoute> findByDeliveryIdAndSequenceAndDriverId(UUID deliveryId,
        Integer sequence, UUID driverId){
        return findByDelivery_IdAndSequenceAndDeliveryDriver_IdAndDeletedAtIsNull(deliveryId, sequence, driverId);
    }
}
