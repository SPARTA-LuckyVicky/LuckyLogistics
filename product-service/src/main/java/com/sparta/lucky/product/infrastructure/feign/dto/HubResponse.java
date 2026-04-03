package com.sparta.lucky.product.infrastructure.feign.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class HubResponse {
    private UUID hubId;
    private UUID managerId;
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
    private UUID createdBy;
}
