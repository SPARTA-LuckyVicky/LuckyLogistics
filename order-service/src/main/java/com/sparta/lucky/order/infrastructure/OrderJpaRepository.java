package com.sparta.lucky.order.infrastructure;

import com.sparta.lucky.order.domain.Order;
import com.sparta.lucky.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    // 업체 담당자용
    Page<Order> findByRequesterCompanyIdOrReceiverCompanyIdAndStatus(
            UUID requesterCompanyId, UUID receiverCompanyId,
            OrderStatus status, Pageable pageable);

    Page<Order> findByRequesterCompanyIdOrReceiverCompanyId(
            UUID requesterCompanyId, UUID receiverCompanyId,
            Pageable pageable);

    // 허브 매니저용
    Page<Order> findByOriginHubIdOrDestinationHubIdAndStatus(
            UUID originHubId, UUID destinationHubId,
            OrderStatus status, Pageable pageable);

    Page<Order> findByOriginHubIdOrDestinationHubId(
            UUID originHubId, UUID destinationHubId,
            Pageable pageable);


}
