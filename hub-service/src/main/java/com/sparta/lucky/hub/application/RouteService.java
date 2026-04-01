package com.sparta.lucky.hub.application;

import com.sparta.lucky.hub.application.dto.GetHubResult;
import com.sparta.lucky.hub.application.dto.GetRouteResult;
import com.sparta.lucky.hub.common.exception.BusinessException;
import com.sparta.lucky.hub.common.exception.HubErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final HubService hubService;

    @Transactional(readOnly = true)
    public GetRouteResult getRoute(UUID originHubId, BigDecimal destinationLat, BigDecimal destinationLong) {

        // 1. destinationLat, destinationLong 기준으로 가장 가까운 destinationHub 찾기
        List<GetHubResult> hubs = hubService.getAllHubs();
        UUID destinationHubId = findNearestHub(hubs, destinationLat, destinationLong).getId();

        // 2. originHub에서 destinationHub까지 경로 찾기
        // TODO: Dijkstra 구현 예정

        return GetRouteResult.of(originHubId, destinationHubId, 100, 100, List.of());
    }

    private GetHubResult findNearestHub(List<GetHubResult> hubs, BigDecimal targetLat, BigDecimal targetLong) {
        return hubs.stream()
                .min(Comparator.comparingDouble(hub -> distanceTo(hub, targetLat, targetLong)))
                .orElseThrow(() -> new BusinessException(HubErrorCode.HUB_NOT_FOUND));
    }

    private double distanceTo(GetHubResult hub, BigDecimal targetLat, BigDecimal targetLong) {
        return haversine(
                hub.getLatitude().doubleValue(), hub.getLongitude().doubleValue(),
                targetLat.doubleValue(), targetLong.doubleValue()
        );
    }

    // Haversine 공식으로 두 좌표 간 거리(km) 계산
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}