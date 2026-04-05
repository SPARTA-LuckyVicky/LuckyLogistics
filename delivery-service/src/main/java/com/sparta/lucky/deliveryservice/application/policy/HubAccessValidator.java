package com.sparta.lucky.deliveryservice.application.policy;

import com.sparta.lucky.deliveryservice.common.error.exceptions.ForbiddenException;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import com.sparta.lucky.deliveryservice.infrastructure.client.UserClient;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HubAccessValidator {

    private final UserClient userClient;

    /**
     * 요청을 보낸 사용자({@code Role.HUB_MANAGER})가 본인이 소속된 허브에 대한
     * 배송 관련 데이터에 접근하는 것이 맞는지 검증합니다.
     * @param accessId 요청을 보낸 사용자 ID
     * @param hubId 목표 hub ID
     */
    public void validateSameHubOrThrow(UUID accessId, UUID hubId) {
        UUID userHubId = userClient.getUser(accessId).hubId();
        if (!hubId.equals(userHubId)) {
            throw new ForbiddenException(ResponseCode.FORBIDDEN);
        }
    }
}
