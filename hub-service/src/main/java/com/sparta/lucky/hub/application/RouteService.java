package com.sparta.lucky.hub.application;

import com.sparta.lucky.hub.application.dto.GetRouteResult;
import com.sparta.lucky.hub.common.exception.BusinessException;
import com.sparta.lucky.hub.common.exception.HubErrorCode;
import com.sparta.lucky.hub.domain.Hub;
import com.sparta.lucky.hub.domain.HubRoute;
import com.sparta.lucky.hub.infrastructure.HubRepository;
import com.sparta.lucky.hub.infrastructure.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final HubService hubService;
    private final HubRouteRepository hubRouteRepository;

    @Transactional(readOnly = true)
    public GetRouteResult getRoute(UUID originHubId, BigDecimal destinationLat, BigDecimal destinationLong) {

        // 1. destinationLat, destinationLong 기준으로 가장 가까운 destinationHub 찾기
        List<Hub> hubs = hubService.getHubsList();
        Hub destinationHub = hubs.get(0);

        // 2. originHub에서 destinationHub까지 경로 찾기 (Dijkstra, duration 기준 최단)
        // TEST용
        int totalDuration = 100;
        int totalDistance = 100;
        LinkedList<UUID> route = new LinkedList<>();

        return GetRouteResult.of(
                originHubId,
                destinationHub.getId(),
                totalDuration,
                totalDistance,
                route
        );
    }
}