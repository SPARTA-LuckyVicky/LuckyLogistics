package com.sparta.lucky.hub.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.lucky.hub.domain.Hub;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class GetHubResult implements Serializable {

    private final UUID id;
    private final UUID managerId;
    private final String name;
    private final String address;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final LocalDateTime createdAt;
    private final UUID createdBy;

    @JsonCreator
    public GetHubResult(
            @JsonProperty("id") UUID id,
            @JsonProperty("managerId") UUID managerId,
            @JsonProperty("name") String name,
            @JsonProperty("address") String address,
            @JsonProperty("latitude") BigDecimal latitude,
            @JsonProperty("longitude") BigDecimal longitude,
            @JsonProperty("createdAt") LocalDateTime createdAt,
            @JsonProperty("createdBy") UUID createdBy
    ) {
        this.id = id;
        this.managerId = managerId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public static GetHubResult from(Hub hub) {
        return new GetHubResult(
                hub.getId(),
                hub.getManagerId(),
                hub.getName(),
                hub.getAddress(),
                hub.getLatitude(),
                hub.getLongitude(),
                hub.getCreatedAt(),
                hub.getCreatedBy()
        );
    }
}
