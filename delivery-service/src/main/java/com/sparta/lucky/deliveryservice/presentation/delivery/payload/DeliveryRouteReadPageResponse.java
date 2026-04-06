package com.sparta.lucky.deliveryservice.presentation.delivery.payload;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryRouteReadResult;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record DeliveryRouteReadPageResponse(
    List<DeliveryRouteReadResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static DeliveryRouteReadPageResponse from(Page<DeliveryRouteReadResult> page) {
        return DeliveryRouteReadPageResponse.builder()
            .content(
                page.getContent().stream().map(DeliveryRouteReadResponse::from).toList()
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
