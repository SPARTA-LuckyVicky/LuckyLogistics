package com.sparta.lucky.order.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CompanyResponse {
    private UUID id;
    private String name;
    private String companyType;
    private UUID hubId;
    private UUID manager;
    private String address;
}