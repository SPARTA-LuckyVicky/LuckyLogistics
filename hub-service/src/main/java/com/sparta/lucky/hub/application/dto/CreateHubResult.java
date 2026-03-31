package com.sparta.lucky.hub.application.dto;

import com.sparta.lucky.hub.domain.Hub;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CreateHubResult {

    private final UUID id;
    private final String name;
    private final String address;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final LocalDateTime createdAt;
    private final UUID createdBy;

    private CreateHubResult(Hub hub) {
        this.id = hub.getId();
        this.name = hub.getName();
        this.address = hub.getAddress();
        this.latitude = hub.getLatitude();
        this.longitude = hub.getLongitude();
        this.createdAt = hub.getCreatedAt();
        this.createdBy = hub.getCreatedBy();
    }

    public static CreateHubResult from(Hub hub) {
        return new CreateHubResult(hub);
    }
}