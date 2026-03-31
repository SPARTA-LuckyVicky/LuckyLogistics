package com.sparta.lucky.hub.presentation.dto;

import com.sparta.lucky.hub.application.dto.GetHubResult;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class GetHubResDto {

    private final UUID hubId;
    private final UUID managerId;
    private final String name;
    private final String address;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final LocalDateTime createdAt;
    private final UUID createdBy;

    private GetHubResDto(GetHubResult result) {
        this.hubId = result.getId();
        this.managerId = result.getManagerId();
        this.name = result.getName();
        this.address = result.getAddress();
        this.latitude = result.getLatitude();
        this.longitude = result.getLongitude();
        this.createdAt = result.getCreatedAt();
        this.createdBy = result.getCreatedBy();
    }

    public static GetHubResDto from(GetHubResult result) {
        return new GetHubResDto(result);
    }
}