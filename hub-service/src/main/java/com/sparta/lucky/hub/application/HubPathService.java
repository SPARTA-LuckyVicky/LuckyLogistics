package com.sparta.lucky.hub.application;

import com.sparta.lucky.hub.application.dto.GetRouteResult;
import com.sparta.lucky.hub.common.exception.BusinessException;
import com.sparta.lucky.hub.common.exception.HubErrorCode;
import com.sparta.lucky.hub.domain.HubRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HubPathService {

    private final RouteService routeService;

    @Transactional(readOnly = true)
    public GetRouteResult getRoute(UUID originHubId, UUID destinationHubId) {

        // 출발 허브 == 도착 허브인 경우
        if (originHubId.equals(destinationHubId)) {
            return GetRouteResult.of(originHubId, destinationHubId, 0, 0, List.of(originHubId));
        }

        // Dijkstra로 최단 경로 탐색 (캐시 경유)
        List<HubRoute> routes = routeService.getHubRoutes();
        PathResult result = findShortestPath(routes, originHubId, destinationHubId);

        return GetRouteResult.of(originHubId, destinationHubId, result.totalDuration(), result.totalDistance(), result.path());
    }


    private record PathResult(List<UUID> path, int totalDistance, int totalDuration) {}

    // 시작 Hub에서 도착 Hub 최단거리 찾기
    private PathResult findShortestPath(List<HubRoute> routes, UUID originId, UUID destinationId) {
        // 양방향 그래프 구성: 두 허브 간 연결은 양방향으로 취급
        Map<UUID, List<HubRoute>> graph = new HashMap<>();
        for (HubRoute route : routes) {
            graph.computeIfAbsent(route.getOriginHubId(), k -> new ArrayList<>()).add(route);
            graph.computeIfAbsent(route.getDestinationHubId(), k -> new ArrayList<>()).add(route.reverse());
        }

        Map<UUID, Integer> distMap = new HashMap<>();
        Map<UUID, Integer> duraMap = new HashMap<>();
        Map<UUID, UUID> prev = new HashMap<>();
        PriorityQueue<UUID> pq = new PriorityQueue<>(
                Comparator.comparingInt(id -> distMap.getOrDefault(id, Integer.MAX_VALUE))
        );

        distMap.put(originId, 0);
        duraMap.put(originId, 0);
        pq.offer(originId);

        while (!pq.isEmpty()) {
            UUID cur = pq.poll();

            if (cur.equals(destinationId)) break;

            int currentDist = distMap.getOrDefault(cur, Integer.MAX_VALUE);

            for (HubRoute edge : graph.getOrDefault(cur, List.of())) {
                UUID next = edge.getDestinationHubId();
                int newDist = currentDist + edge.getDistance();

                if (newDist < distMap.getOrDefault(next, Integer.MAX_VALUE)) {
                    distMap.put(next, newDist);
                    duraMap.put(next, duraMap.getOrDefault(cur, 0) + edge.getDuration());
                    prev.put(next, cur);
                    pq.offer(next);
                }
            }
        }

        if (!distMap.containsKey(destinationId)) {
            throw new BusinessException(HubErrorCode.HUB_ROUTE_NOT_FOUND);
        }

        return new PathResult(
                reconstructPath(prev, originId, destinationId),
                distMap.get(destinationId),
                duraMap.get(destinationId)
        );
    }

    private List<UUID> reconstructPath(Map<UUID, UUID> prev, UUID originId, UUID destinationId) {
        LinkedList<UUID> path = new LinkedList<>();
        UUID cur = destinationId;

        while (cur != null) {
            path.addFirst(cur);
            cur = prev.get(cur);
        }

        if (path.isEmpty() || !path.getFirst().equals(originId)) {
            throw new BusinessException(HubErrorCode.HUB_ROUTE_NOT_FOUND);
        }

        return Collections.unmodifiableList(path);
    }
}