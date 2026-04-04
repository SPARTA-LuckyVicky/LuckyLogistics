package com.sparta.lucky.deliveryservice.common.security.auth;

import com.sparta.lucky.deliveryservice.common.code.Role;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ExternalUserPrincipal {

    private final UUID userId;
    private final Role role;

    public ExternalUserPrincipal(UUID userId, Role role) {
        this.userId = userId;
        this.role = role;
    }
}
