package com.sparta.lucky.order.domain;

import com.sparta.lucky.order.common.entity.BaseEntity;
import com.sparta.lucky.order.common.exception.BusinessException;
import com.sparta.lucky.order.common.exception.OrderErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_order", schema = "order_schema")
@SQLRestriction("deleted_at IS NULL") // Soft Delete
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID requesterCompanyId;

    @Column(nullable = false)
    private UUID receiverCompanyId;

    @Column(nullable = false)
    private UUID productId;

    // 스냅샷
    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false)
    private Integer totalPrice;

    private UUID deliveryId;

    // 알림용 저장
    private UUID originHubId;
    private UUID destinationHubId;
    private String originHubName;
    private String destinationHubName;
    private String deliveryAddress;
    private String recipientName;
    private String recipientSlackId;
    private String hubManagerSlackId;

    @Column(columnDefinition = "TEXT")
    private String requestNote;

    private LocalDateTime requestedDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // 주문 생성 메서드
    public static Order create(
            UUID requesterCompanyId,
            UUID receiverCompanyId,
            UUID productId,
            String productName,
            Integer quantity,
            Integer unitPrice,
            String requestNote,
            LocalDateTime requestedDeadline
    ) {
        // 생성 시점에서 수량과 가격 검증
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(OrderErrorCode.ORDER_INVALID_QUANTITY);
        }
        if (unitPrice == null || unitPrice <= 0) {
            throw new BusinessException(OrderErrorCode.ORDER_INVALID_PRICE);
        }

        Order order = new Order();
        order.requesterCompanyId = requesterCompanyId;
        order.receiverCompanyId = receiverCompanyId;
        order.productId = productId;
        order.productName = productName;
        order.quantity = quantity;
        order.unitPrice = unitPrice;
        order.totalPrice = unitPrice * quantity;
        order.requestNote = requestNote;
        order.requestedDeadline = requestedDeadline;
        order.status = OrderStatus.CREATED;
        return order;
    }

    // 배송 정보 받아오면 정보 업데이트
    public void updateDeliveryInfo(
            UUID deliveryId,
            UUID originHubId,       // 추가
            UUID destinationHubId,  // 추가
            String originHubName,
            String destinationHubName,
            String deliveryAddress,
            String recipientName,
            String recipientSlackId,
            String hubManagerSlackId
    ) {
        assertEditable();
        this.deliveryId = deliveryId;
        this.originHubName = originHubName;
        this.destinationHubName = destinationHubName;
        this.originHubId = originHubId;         // 추가
        this.destinationHubId = destinationHubId; // 추가
        this.deliveryAddress = deliveryAddress;
        this.recipientName = recipientName;
        this.recipientSlackId = recipientSlackId;
        this.hubManagerSlackId = hubManagerSlackId;
    }

    // 수정
    public void update(String requestNote, LocalDateTime requestedDeadline) {
        assertEditable();
        if (requestNote != null) this.requestNote = requestNote;
        if (requestedDeadline != null) this.requestedDeadline = requestedDeadline;
    }

    // 주문 취소 (상태만 변경,노출)
    public void cancel() {
        assertEditable();
        this.status = OrderStatus.CANCELLED;
    }

    // 주문 삭제 (비노출)
    public void softDelete(UUID deletedBy) {
        assertDeletable();
        super.softDelete(deletedBy);
    }

    // 완료
    public void complete() {
        assertEditable();
        this.status = OrderStatus.COMPLETED;
    }

    // 상태 확인(CREATED) 메서드
    private void assertEditable() {
        if (this.status == OrderStatus.CANCELLED || this.status == OrderStatus.COMPLETED) {
            throw new BusinessException(OrderErrorCode.ORDER_CANNOT_BE_MODIFIED);
        }
    }
    // 삭제 가능 상태(CANCELLED/COMPLETED) 확인
    private void assertDeletable() {
        if (this.status == OrderStatus.CREATED) {
            throw new BusinessException(OrderErrorCode.ORDER_CANNOT_BE_DELETED);
        }
    }
}
