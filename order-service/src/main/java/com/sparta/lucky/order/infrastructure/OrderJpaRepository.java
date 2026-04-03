package com.sparta.lucky.order.infrastructure;

import com.sparta.lucky.order.domain.Order;
import com.sparta.lucky.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
