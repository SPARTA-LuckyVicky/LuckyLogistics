package com.sparta.lucky.deliveryservice.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyResponse {

    public CompanyData data;

    // 기존 호출부(DeliveryService) 변경 없이 호환
    public UUID hubId() { return data != null ? data.hubId : null; }
    public String address() { return data != null ? data.address : null; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompanyData {
        public UUID hubId;
        public String address;
    }
}
