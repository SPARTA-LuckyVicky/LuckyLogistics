package com.sparta.lucky.order.infrastructure;

import com.sparta.lucky.order.domain.Order;
import com.sparta.lucky.order.domain.OrderRepository;
import com.sparta.lucky.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable);
    }

    @Override
    public Page<Order> findByStatus(OrderStatus status, Pageable pageable) {
        return jpaRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Order> findByRequesterCompanyIdOrReceiverCompanyId(
            UUID requesterCompanyId, UUID receiverCompanyId,
            OrderStatus status, Pageable pageable) {
        if (status != null) {
            return jpaRepository.findByRequesterCompanyIdOrReceiverCompanyIdAndStatus(
                    requesterCompanyId, receiverCompanyId, status, pageable);
        }
        return jpaRepository.findByRequesterCompanyIdOrReceiverCompanyId(
                requesterCompanyId, receiverCompanyId, pageable);
    }

    @Override
    public Page<Order> findByOriginHubNameOrDestinationHubName(
            String originHubName, String destinationHubName,
            OrderStatus status, Pageable pageable) {
        if (status != null) {
            return jpaRepository.findByOriginHubNameOrDestinationHubNameAndStatus(
                    originHubName, destinationHubName, status, pageable);
        }
        return jpaRepository.findByOriginHubNameOrDestinationHubName(
                originHubName, destinationHubName, pageable);
    }

}
