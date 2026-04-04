package com.sparta.lucky.order.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    Page<Order> findAll(Pageable pageable);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // 업체 담당자용
    Page<Order> findByRequesterCompanyIdOrReceiverCompanyId(
            UUID requesterCompanyId, UUID receiverCompanyId,
            OrderStatus status, Pageable pageable);

    // 허브 매니저용 - 기존 허브명 조회 제거하고 hubId 조회로 교체
    Page<Order> findByOriginHubIdOrDestinationHubId(
            UUID originHubId, UUID destinationHubId,
            OrderStatus status, Pageable pageable);

    // 배송 담당자용
    Page<Order> findByDeliveryIdIn(List<UUID> deliveryIds, Pageable pageable);
    Page<Order> findByDeliveryIdInAndStatus(List<UUID> deliveryIds, OrderStatus status, Pageable pageable);


}