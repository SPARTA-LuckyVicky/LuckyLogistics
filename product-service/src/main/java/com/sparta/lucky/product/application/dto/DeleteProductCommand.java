package com.sparta.lucky.product.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DeleteProductCommand {
    private final UUID productId;
    private final UUID requesterId;
    private final String requesterRole;
    private final UUID requesterHubId;
}