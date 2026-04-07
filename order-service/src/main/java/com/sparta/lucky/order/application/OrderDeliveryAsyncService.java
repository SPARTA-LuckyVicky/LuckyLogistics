package com.sparta.lucky.order.application;

import com.sparta.lucky.order.application.dto.CreateOrderCommand;
import com.sparta.lucky.order.domain.Order;
import com.sparta.lucky.order.domain.OrderRepository;
import com.sparta.lucky.order.infrastructure.client.DeliveryClient;
import com.sparta.lucky.order.infrastructure.client.ProductClient;
import com.sparta.lucky.order.infrastructure.client.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDeliveryAsyncService {

    private final DeliveryClient deliveryClient;
    private final ProductClient productClient;
    private final OrderRepository orderRepository;

    private static final String INTERNAL_REQUEST = "true";

    // ⭐️ @Async: 이 메서드는 누군가 부르면, 백그라운드 스레드에서 알아서 돕니다!
    @Async
    @Transactional // 백그라운드에서 새로운 트랜잭션으로 DB에 접근
    public void processDeliveryInBackground(
            UUID orderId, CreateOrderCommand request, ProductResponse product,
            CompanyResponse receiverCompany, HubResponse originHub, HubResponse destHub,
            UserResponse user, UserResponse hubManager) {

        log.info("[비동기 배송 처리 시작] Order ID: {}", orderId);

        try {
            // 1. 배송 서버 호출 (시간이 오래 걸려도 상관없음!)
            DeliveryCreateResponse delivery = deliveryClient.createDelivery(
                    new DeliveryCreateRequest(
                            orderId,
                            request.getReceiverCompanyId(),
                            product.getHubId(),
                            user != null ? user.getName() : "미확인",
                            user != null ? user.getReceiverSlackId() : ""
                    ), INTERNAL_REQUEST).getData();

            // 2. 주문 정보를 다시 DB에서 꺼내서 배송 정보 업데이트
            Order order = orderRepository.findById(orderId).orElseThrow();
            order.updateDeliveryInfo(
                    delivery.getDeliveryId(),
                    originHub.getHubId(), destHub.getHubId(),
                    originHub.getName(), destHub.getName(),
                    receiverCompany.getAddress(),
                    user != null ? user.getName() : "미확인",
                    user != null ? user.getReceiverSlackId() : "",
                    hubManager != null ? hubManager.getReceiverSlackId() : ""
            );
            // JPA 변경 감지(Dirty Checking)로 자동 Update 쿼리 날아감
            log.info("[비동기 배송 처리 성공] Delivery ID: {}", delivery.getDeliveryId());

        } catch (Exception ex) {
            log.error("[비동기 배송 처리 실패] Order ID: {} - 보상 트랜잭션(재고 복원) 시작", orderId, ex);
            // 보상 트랜잭션: 배송 실패 시 재고 원복
            productClient.restoreStock(
                    request.getProductId(),
                    new StockUpdateRequest(request.getQuantity()),
                    INTERNAL_REQUEST
            );

            // 주의: 이미 사용자에게 200 OK가 나갔으므로 여기서 Exception을 던져도 사용자는 모릅니다.
            // 실무에서는 여기서 주문 상태를 '배송_실패' 같은 에러 상태로 변경하고 관리자에게 슬랙 알림을 보냅니다.
            Order order = orderRepository.findById(orderId).orElseThrow();
            // order.markAsDeliveryFailed(); // 필요하다면 이런 메서드를 도메인에 추가!
        }
    }
}