package com.sparta.lucky.hub.application;

import com.sparta.lucky.hub.application.dto.GetRouteResult;
import com.sparta.lucky.hub.domain.HubRoute;
import com.sparta.lucky.hub.infrastructure.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final HubService hubService;
    private final HubRouteRepository hubRouteRepository;
    private final HubToHubPathFinder hubToHubPathFinder;

    @Transactional(readOnly = true)
    public GetRouteResult getRoute(UUID originHubId, UUID destinationHubId) {

        // 출발 허브 == 도착 허브인 경우
        if (originHubId.equals(destinationHubId)) {
            return GetRouteResult.of(originHubId, destinationHubId, 0, 0, List.of(originHubId));
        }

        // Dijkstra로 최단 경로 탐색
        List<HubRoute> routes = hubRouteRepository.findAllByDeletedAtIsNull();
        HubToHubPathFinder.PathResult result = hubToHubPathFinder.findShortestPath(routes, originHubId, destinationHubId);

        return GetRouteResult.of(originHubId, destinationHubId, result.totalDuration(), result.totalDistance(), result.path());
    }

    @Transactional(readOnly = true)
    public List<HubRoute> getHubRoutes() {
        return hubRouteRepository.findAllByDeletedAtIsNull();
    }

    @Transactional
    public void saveHubRoute(HubRoute hubRoute) {
        hubRouteRepository.save(hubRoute);
    }
}