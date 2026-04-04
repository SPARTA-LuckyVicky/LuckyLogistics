package com.sparta.lucky.order.infrastructure;

import com.sparta.lucky.order.domain.Order;
import com.sparta.lucky.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // 업체 담당자용
    @Query("""
            SELECT o FROM Order o
            WHERE (o.requesterCompanyId = :requesterCompanyId OR o.receiverCompanyId = :receiverCompanyId)
            AND o.status = :status
            """)
    Page<Order> findByRequesterCompanyIdOrReceiverCompanyIdAndStatus(
            @Param("requesterCompanyId") UUID requesterCompanyId,
            @Param("receiverCompanyId") UUID receiverCompanyId,
            @Param("status") OrderStatus status,
            Pageable pageable);

    Page<Order> findByRequesterCompanyIdOrReceiverCompanyId(
            UUID requesterCompanyId, UUID receiverCompanyId,
            Pageable pageable);

    // 허브 매니저용
    @Query("""
            SELECT o FROM Order o
            WHERE (o.originHubId = :originHubId OR o.destinationHubId = :destinationHubId)
            AND o.status = :status
            """)
    Page<Order> findByOriginHubIdOrDestinationHubIdAndStatus(
            @Param("originHubId") UUID originHubId,
            @Param("destinationHubId") UUID destinationHubId,
            @Param("status") OrderStatus status,
            Pageable pageable);

    Page<Order> findByOriginHubIdOrDestinationHubId(
            UUID originHubId, UUID destinationHubId,
            Pageable pageable);

    // 배송 담당자용
    Page<Order> findByDeliveryIdIn(List<UUID> deliveryIds, Pageable pageable);

    @Query("""
        SELECT o FROM Order o
        WHERE o.deliveryId IN :deliveryIds
        AND o.status = :status
        """)
    Page<Order> findByDeliveryIdInAndStatus(
            @Param("deliveryIds") List<UUID> deliveryIds,
            @Param("status") OrderStatus status,
            Pageable pageable);

}