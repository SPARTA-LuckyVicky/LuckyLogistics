package com.sparta.lucky.order.application;

import com.sparta.lucky.order.application.dto.request.CreateOrderCommand;
import com.sparta.lucky.order.application.dto.request.UpdateOrderCommand;
import com.sparta.lucky.order.application.dto.response.OrderResponse;
import com.sparta.lucky.order.domain.Order;
import com.sparta.lucky.order.domain.OrderRepository;
import com.sparta.lucky.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    // 주문 생성
    @Transactional
    public OrderResponse createOrder(CreateOrderCommand request) {

        // TODO: FeignClient 연동 후 실제 값으로 교체
        // 주문 당시의 상품 이름&가격 으로 저장
        String productName = "더미상품";
        BigDecimal unitPrice = BigDecimal.valueOf(1000);

        Order order = Order.create(
                request.getRequesterCompanyId(),
                request.getReceiverCompanyId(),
                request.getProductId(),
                productName,
                request.getQuantity(),
                unitPrice,
                request.getRequestNote(),
                request.getRequestedDeadline()
        );

        // TODO: FeignClient 연동 후 delivery-service, hub-service 호출 -> 실제 응답으로 교체
        order.updateDeliveryInfo(
                UUID.randomUUID(),      // deliveryId
                "더미출발허브",           // originHubName
                "더미도착허브",           // destinationHubName
                "더미배송주소",           // deliveryAddress
                "더미수신자",            // recipientName
                "더미수신자슬랙ID",            // recipientSlackId
                "더미허브매니저슬랙ID"    // hubManagerSlackId
        );

        return OrderResponse.from(orderRepository.save(order));
    }

    // 주문 목록 조회 (페이징 + status 필터)
    public Page<OrderResponse> getOrders(OrderStatus status, Pageable pageable) {
        if (status != null) {
            return orderRepository.findByStatus(status, pageable)
                    .map(OrderResponse::from);
        }
        return orderRepository.findAll(pageable)
                .map(OrderResponse::from);
    }

    // 주문 단건 조회
    public OrderResponse getOrder(UUID id) {
        return OrderResponse.from(findOrderById(id));
    }

    // 주문 수정
    @Transactional
    public OrderResponse updateOrder(UUID id, UpdateOrderCommand request) {
        Order order = findOrderById(id);
        order.update(request.getRequestNote(), request.getRequestedDeadline());
        return OrderResponse.from(order);
    }

    // 주문 취소 (상태 변경, 노출 유지)
    @Transactional
    public OrderResponse cancelOrder(UUID id) {
        Order order = findOrderById(id);
        // TODO: FeignClient 연동 후 배송 상태 체크하는 로직 추가
        order.cancel();
        return OrderResponse.from(order);
    }

    // 주문 삭제 (soft delete, 비노출)
    @Transactional
    public void deleteOrder(UUID id, String deletedBy) {
        Order order = findOrderById(id);
        // 나중에 JWT 연동 시 수정 필요
        // 지금은 defaultValue = "system"
        order.softDelete(deletedBy);
    }

    // [내부 API] delivery-service → 배송 완료 시 호출 하면 주문 COMPLETED 처리
    @Transactional
    public OrderResponse completeOrder(UUID id) {
        Order order = findOrderById(id);
        order.complete();
        return OrderResponse.from(order);
    }

    // 공통: ID로 주문 조회
    private Order findOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. id=" + id));
    }
}
