package com.sparta.lucky.order.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class HubResponse {
    private UUID hubId;
    private UUID managerId;
    private String name;
    private String address;
}