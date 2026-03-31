package com.sparta.lucky.hub.application.dto;

import com.sparta.lucky.hub.domain.Hub;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class GetHubResult {

    private final UUID id;
    private final UUID managerId;
    private final String name;
    private final String address;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final LocalDateTime createdAt;
    private final UUID createdBy;

    private GetHubResult(Hub hub) {
        this.id = hub.getId();
        this.managerId = hub.getManagerId();
        this.name = hub.getName();
        this.address = hub.getAddress();
        this.latitude = hub.getLatitude();
        this.longitude = hub.getLongitude();
        this.createdAt = hub.getCreatedAt();
        this.createdBy = hub.getCreatedBy();
    }

    public static GetHubResult from(Hub hub) {
        return new GetHubResult(hub);
    }
}
