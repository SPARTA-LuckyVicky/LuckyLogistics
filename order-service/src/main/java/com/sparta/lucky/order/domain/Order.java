package com.sparta.lucky.order.domain;

import com.sparta.lucky.order.common.entity.BaseEntity;
import com.sparta.lucky.order.domain.OrderStatus;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
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
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    private UUID deliveryId;

    // 알림용 저장
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
            BigDecimal unitPrice,
            String requestNote,
            LocalDateTime requestedDeadline
    ) {
        Order order = new Order();
        order.requesterCompanyId = requesterCompanyId;
        order.receiverCompanyId = receiverCompanyId;
        order.productId = productId;
        order.productName = productName;
        order.quantity = quantity;
        order.unitPrice = unitPrice;
        order.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        order.requestNote = requestNote;
        order.requestedDeadline = requestedDeadline;
        order.status = OrderStatus.CREATED;
        return order;
    }

    // 배송 정보 받아오면 정보 업데이트
    public void updateDeliveryInfo(
            UUID deliveryId,
            String originHubName,
            String destinationHubName,
            String deliveryAddress,
            String recipientName,
            String recipientSlackId,
            String hubManagerSlackId
    ) {
        this.deliveryId = deliveryId;
        this.originHubName = originHubName;
        this.destinationHubName = destinationHubName;
        this.deliveryAddress = deliveryAddress;
        this.recipientName = recipientName;
        this.recipientSlackId = recipientSlackId;
        this.hubManagerSlackId = hubManagerSlackId;
    }

    // 수정
    public void update(String requestNote, LocalDateTime requestedDeadline) {
        if (requestNote != null) this.requestNote = requestNote;
        if (requestedDeadline != null) this.requestedDeadline = requestedDeadline;
    }

    // 취소
    public void cancel(String deletedBy) {
        this.status = OrderStatus.CANCELLED;
        this.delete(deletedBy);
    }

    // 완료
    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }
}
