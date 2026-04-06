package com.sparta.lucky.deliveryservice.presentation.driver.payload;

import com.sparta.lucky.deliveryservice.application.dto.DeliveryDriverReadResult;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record DeliveryDriverReadPageResponse(
    List<DeliveryDriverReadResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static DeliveryDriverReadPageResponse from(Page<DeliveryDriverReadResult> page) {
        return DeliveryDriverReadPageResponse.builder()
            .content(
                page.getContent().stream().map(DeliveryDriverReadResponse::fromResult).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}
