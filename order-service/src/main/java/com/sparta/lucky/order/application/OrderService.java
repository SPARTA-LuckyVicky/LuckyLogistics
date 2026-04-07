package com.sparta.lucky.order.application;

import com.sparta.lucky.order.application.dto.CreateOrderCommand;
import com.sparta.lucky.order.application.dto.OrderInternalResponse;
import com.sparta.lucky.order.application.dto.OrderResponse;
import com.sparta.lucky.order.application.dto.UpdateOrderCommand;
import com.sparta.lucky.order.common.exception.BusinessException;
import com.sparta.lucky.order.common.exception.OrderErrorCode;
import com.sparta.lucky.order.domain.Order;
import com.sparta.lucky.order.domain.OrderRepository;
import com.sparta.lucky.order.domain.OrderStatus;
import com.sparta.lucky.order.infrastructure.client.*;
import com.sparta.lucky.order.infrastructure.client.dto.*;
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
    private final ProductClient productClient;
    private final CompanyClient companyClient;
    private final HubClient hubClient;
    private final UserClient userClient;
    private final DeliveryClient deliveryClient;

    private static final String INTERNAL_REQUEST = "true";
    // 주문 생성
    @Transactional
    public OrderResponse createOrder(CreateOrderCommand request,String userId,String role) {

        // 1. 상품 조회 → productName, unitPrice, originHubId 확보
        ProductResponse product = productClient
                .getProduct(request.getProductId(),INTERNAL_REQUEST)
                .getData();
        if (product == null) {
            throw new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND);
        }

        // 재고 차감 후 순위로 미룸

        // 업체에서 요청한 주문일 때는 해당 검증 확인
        if ("COMPANY_MANAGER".equals(role)) {
            UserResponse user = userClient
                    .getUser(UUID.fromString(userId), INTERNAL_REQUEST)
                    .getData();
            if (user == null || user.getCompanyId() == null) {
                throw new BusinessException(OrderErrorCode.COMPANY_NOT_FOUND);
            }
            // 본인 업체가 수령 업체인지 확인
            if (!user.getCompanyId().equals(request.getReceiverCompanyId())) {
                throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
            }
        }

        // 3-1. 허브 조회 : 수령 업체 조회 → 도착 허브(destinationHubName), 도착 업체 주소(deliveryAddress) 확보
        CompanyResponse receiverCompany = companyClient
                .getCompany(request.getReceiverCompanyId(), INTERNAL_REQUEST)
                .getData();
        if (receiverCompany == null) {
            throw new BusinessException(OrderErrorCode.COMPANY_NOT_FOUND);
        }

        // 3-2. 허브 조회 : 출발 허브 조회 → originHubName 확보
        HubResponse originHub = hubClient
                .getHub(product.getHubId(), INTERNAL_REQUEST)
                .getData();
        if (originHub == null) {
            throw new BusinessException(OrderErrorCode.HUB_NOT_FOUND);
        }

        // 3-3. 허브 조회 : 도착 허브 조회 → destHubName, hubManagerId 확보
        HubResponse destHub = hubClient
                .getHub(receiverCompany.getHubId(),INTERNAL_REQUEST)
                .getData();
        if (destHub == null) {
            throw new BusinessException(OrderErrorCode.HUB_NOT_FOUND);
        }

        // 4. 유저 조회 : 수령자 조회 → recipientName, recipientSlackId 확보
        UserResponse recipient = userClient
                .getUser(receiverCompany.getManager(), INTERNAL_REQUEST)
                .getData();

        // 4-1. 유저 조회 : 출발 허브 매니저 슬랙ID 조회
        UserResponse hubManager = userClient
                .getUser(originHub.getManagerId(), INTERNAL_REQUEST)
                .getData();

        // 5. 주문 생성
        Order order = Order.create(
                product.getCompanyId(),
                request.getReceiverCompanyId(),
                request.getProductId(),
                product.getName(),
                request.getQuantity(),
                product.getPrice(),
                request.getRequestNote(),
                request.getRequestedDeadline()
        );

        // 6. 재고 차감 (실패 시 주문 롤백) <- 이동
        StockResponse stock = productClient
                .decreaseStock(request.getProductId(),
                        new StockUpdateRequest(request.getQuantity()),
                        INTERNAL_REQUEST)
                .getData();
        if (stock == null) {
            throw new BusinessException(OrderErrorCode.OUT_OF_STOCK);
        }

        // 7. 배송 생성 → deliveryId 확보
        DeliveryCreateResponse delivery;
        try {
            delivery = deliveryClient
                    .createDelivery(new DeliveryCreateRequest(
                            order.getId(),
                            request.getReceiverCompanyId(),
                            product.getHubId(),
                            recipient != null ? recipient.getName() : "미확인",
                            recipient != null ? recipient.getReceiverSlackId() : ""
                    ), INTERNAL_REQUEST)
                    .getData();
        } catch (RuntimeException ex) {
            // 보상 트랜잭션: 배송 생성 실패 시 재고 복원
            log.error("배송 생성 실패 - 재고 복원 시작", ex);
            productClient.restoreStock(
                    request.getProductId(),
                    new StockUpdateRequest(request.getQuantity()),
                    INTERNAL_REQUEST
            );
            throw new BusinessException(OrderErrorCode.DELIVERY_CREATE_FAILED);
        }
        if (delivery == null || delivery.getDeliveryId() == null) {
            // 보상 트랜잭션: 재고 복원
            productClient.restoreStock(
                    request.getProductId(),
                    new StockUpdateRequest(request.getQuantity()),
                    INTERNAL_REQUEST
            );
            throw new BusinessException(OrderErrorCode.DELIVERY_CREATE_FAILED);
        }

        // 8. 주문에 배송 정보 업데이트
        order.updateDeliveryInfo(
                delivery.getDeliveryId(),
                originHub.getHubId(),
                destHub.getHubId(),
                originHub.getName(),
                destHub.getName(),
                receiverCompany.getAddress(),
                recipient != null ? recipient.getName() : "미확인",
                recipient != null ? recipient.getReceiverSlackId() : "",
                hubManager != null ? hubManager.getReceiverSlackId() : ""
        );

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);

    }

    // 주문 목록 조회 (페이징 + status 필터 + 역할별 조회 결과 다르게)
    // 목록을 조회 하는건 마스터, 허브 매니저, 업체 매니저 만 가능
    public Page<OrderResponse> getOrders(OrderStatus status,
                                         String role,
                                         UUID hubId,
                                         UUID companyId,
                                         UUID userId,
                                         Pageable pageable) {
        if ("MASTER".equals(role)) {
            if (hubId != null && companyId != null) {
                return orderRepository.findByHubAndCompany(
                        hubId, companyId, status, pageable).map(OrderResponse::from);
            }
            if (hubId != null) {
                return orderRepository.findByOriginHubIdOrDestinationHubId(
                        hubId, hubId, status, pageable).map(OrderResponse::from);
            }
            if (companyId != null) {
                return orderRepository.findByRequesterCompanyIdOrReceiverCompanyId(
                        companyId, companyId, status, pageable).map(OrderResponse::from);
            }
            // hubId, companyId 없으면 status만 or 전체
            if (status != null) {
                return orderRepository.findByStatus(status, pageable).map(OrderResponse::from);
            }
            return orderRepository.findAll(pageable).map(OrderResponse::from);

        } else if ("HUB_MANAGER".equals(role)) {
            UserResponse user = userClient.getUser(userId, INTERNAL_REQUEST).getData();
            if (user == null || user.getHubId() == null) {
                throw new BusinessException(OrderErrorCode.HUB_NOT_FOUND);
            }
            // 업체별 필터
            if (companyId != null) {
                return orderRepository.findByHubAndCompany(
                        user.getHubId(), companyId, status, pageable).map(OrderResponse::from);
            }
            return orderRepository.findByOriginHubIdOrDestinationHubId(
                            user.getHubId(), user.getHubId(), status, pageable)
                    .map(OrderResponse::from);
        } else if ("COMPANY_MANAGER".equals(role)) {
            // userId로 user-service 조회 → companyId 확보
            UserResponse user = userClient.getUser(userId, INTERNAL_REQUEST).getData();
            if (user == null || user.getCompanyId() == null) {
                throw new BusinessException(OrderErrorCode.COMPANY_NOT_FOUND);
            }
            return orderRepository.findByRequesterCompanyIdOrReceiverCompanyId(
                            user.getCompanyId(), user.getCompanyId(), status, pageable)
                    .map(OrderResponse::from);
        }
        else {
            // 미허용 역할 → 접근 거부
            throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }
    }

    // 주문 단건 조회
    // 모든 역할 가능
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

        // 1. 먼저 주문 상태 검증 (이미 취소/완료된 주문이면 외부 호출 전에 차단)
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        // 2. 배송 상태 확인
        DeliveryStatusResponse status = deliveryClient
                .getDeliveryStatus(id, INTERNAL_REQUEST)
                .getData();
        if (status == null || !"WAITING".equals(status.getDeliveryStatus())) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        // 3. 재고 복원
        productClient.restoreStock(
                order.getProductId(),
                new StockUpdateRequest(order.getQuantity()),
                INTERNAL_REQUEST
        );

        // 4. 배송 취소
        deliveryClient.cancelDelivery(order.getDeliveryId(), INTERNAL_REQUEST);
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

    // [내부 API] 주문 조회 -> notification-service 알림 발송을 위해 orderId로 정보 조회
    public OrderInternalResponse getOrderInternal(UUID id) {
        return OrderInternalResponse.from(findOrderById(id));
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
