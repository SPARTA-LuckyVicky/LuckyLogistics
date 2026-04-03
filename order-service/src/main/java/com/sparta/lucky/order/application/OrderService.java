package com.sparta.lucky.order.application;

import com.sparta.lucky.order.application.dto.request.CreateOrderCommand;
import com.sparta.lucky.order.application.dto.request.UpdateOrderCommand;
import com.sparta.lucky.order.application.dto.response.OrderResponse;
import com.sparta.lucky.order.common.exception.BusinessException;
import com.sparta.lucky.order.common.exception.OrderErrorCode;
import com.sparta.lucky.order.domain.Order;
import com.sparta.lucky.order.domain.OrderRepository;
import com.sparta.lucky.order.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    // 주문 생성
    @Transactional
    public OrderResponse createOrder(CreateOrderCommand request) {

    /*
     * TODO: FeignClient 연동 시 구현 company-service 호출
     * 1. 상품 조회: companyClient.getProduct(productId, "true")
     * 2. 재고 차감: companyClient.decreaseStock(productId, quantity, "true")
     *
     * */
        String productName = "더미상품";
        Integer unitPrice = 1000;

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

    /*
    * TODO: FeignClient 연동 시 구현 delivery-service, hub-service 호출 -> 실제 응답으로 교체
    * 3. 배송 생성: deliveryClient.createDelivery(...)
    * 4. 허브 조회: hubClient.getHub(hubId, "true")
    * */
        order.updateDeliveryInfo(
                UUID.randomUUID(),      // deliveryId
                "더미출발허브",           // originHubName
                "더미도착허브",           // destinationHubName
                "더미배송주소",           // deliveryAddress
                "더미수신자",            // recipientName
                "더미수신자슬랙ID",            // recipientSlackId
                "더미허브매니저슬랙ID"    // hubManagerSlackId
        );
        Order savedOrder = orderRepository.save(order); // ← 저장 먼저!

        /*
         * TODO: FeignClient 연동 시 구현 notification-service
         * 5. 알림 발송: notificationClient.sendOrderAlert(savedOrder.getId(), ...)
         *    → 알림 발송이 실패해도 주문은 성공 (try-catch로 처리)
         */
        return OrderResponse.from(savedOrder);
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
    /*
    * TODO: FeignClient 연동 시 구현
    * 1. 배송 상태 확인: deliveryClient.getDeliveryStatus(orderId)
    *    → WAITING이 아니면 에러
    * 2. 재고 복원: companyClient.restoreStock(productId, quantity, "true")
    * 3. 배송 취소: deliveryClient.cancelDelivery(deliveryId)
    */
        order.cancel();
        return OrderResponse.from(order);
    }

    // 주문 삭제 (soft delete, 비노출)
    @Transactional
    public void deleteOrder(UUID id, UUID deletedBy) {
        Order order = findOrderById(id);
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
        log.debug("주문 조회 - orderId: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("주문 조회 실패 - orderId: {}", id);
                    return new BusinessException(OrderErrorCode.ORDER_NOT_FOUND);
                });
    }
}
