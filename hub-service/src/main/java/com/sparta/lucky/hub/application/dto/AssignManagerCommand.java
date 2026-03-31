package com.sparta.lucky.hub.application.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AssignManagerCommand {

    private final UUID hubId;
    private final UUID managerId;

    private AssignManagerCommand(UUID hubId, UUID managerId) {
        this.hubId = hubId;
        this.managerId = managerId;
    }

    public static AssignManagerCommand of(UUID hubId, UUID managerId) {
        return new AssignManagerCommand(hubId, managerId);
    }
}