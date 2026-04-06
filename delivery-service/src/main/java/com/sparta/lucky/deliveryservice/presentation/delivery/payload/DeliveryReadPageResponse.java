package com.sparta.lucky.deliveryservice.presentation.delivery.payload;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryReadResult;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record DeliveryReadPageResponse(
    List<DeliveryReadResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static DeliveryReadPageResponse from(Page<DeliveryReadResult> page) {
        return DeliveryReadPageResponse.builder()
            .content(
                page.getContent().stream().map(DeliveryReadResponse::from).toList()
            )
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}
