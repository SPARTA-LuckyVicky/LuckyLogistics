package com.sparta.lucky.hub.application;

import com.sparta.lucky.hub.common.exception.BusinessException;
import com.sparta.lucky.hub.common.exception.HubErrorCode;
import com.sparta.lucky.hub.domain.HubRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HubPathCacheService {

    private final HubRouteService hubRouteService;

    /**
     * 다익스트라로 구한 최단 경로를 허브 ID 순서 목록으로 캐싱.
     * 예: 서울 → 대전 → 부산 이면 [서울UUID, 대전UUID, 부산UUID]
     * 시간/거리는 포함하지 않아 routes 캐시의 최신값을 항상 반영 가능.
     */
    @Cacheable(cacheNames = "path", key = "#originHubId + '-' + #destinationHubId", sync = true)
    @Transactional(readOnly = true)
    public List<UUID> getPathHubIds(UUID originHubId, UUID destinationHubId) {
        List<HubRoute> routes = hubRouteService.getHubRoutes();
        return findShortestPathHubIds(routes, originHubId, destinationHubId);
    }

    private List<UUID> findShortestPathHubIds(List<HubRoute> routes, UUID originId, UUID destinationId) {
        Map<UUID, List<HubRoute>> graph = new HashMap<>();
        for (HubRoute route : routes) {
            graph.computeIfAbsent(route.getOriginHubId(), k -> new ArrayList<>()).add(route);
            graph.computeIfAbsent(route.getDestinationHubId(), k -> new ArrayList<>()).add(route.reverse());
        }

        Map<UUID, Integer> distMap = new HashMap<>();
        Map<UUID, UUID> prevHub = new HashMap<>();
        PriorityQueue<UUID> pq = new PriorityQueue<>(
                Comparator.comparingInt(id -> distMap.getOrDefault(id, Integer.MAX_VALUE))
        );

        distMap.put(originId, 0);
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
                    prevHub.put(next, cur);
                    pq.offer(next);
                }
            }
        }

        if (!distMap.containsKey(destinationId)) {
            throw new BusinessException(HubErrorCode.HUB_ROUTE_NOT_FOUND);
        }

        return reconstructHubIds(prevHub, originId, destinationId);
    }

    private List<UUID> reconstructHubIds(Map<UUID, UUID> prevHub, UUID originId, UUID destinationId) {
        LinkedList<UUID> hubIds = new LinkedList<>();
        UUID cur = destinationId;

        while (cur != null) {
            hubIds.addFirst(cur);
            cur = prevHub.get(cur);
        }

        if (!hubIds.getFirst().equals(originId)) {
            throw new BusinessException(HubErrorCode.HUB_ROUTE_NOT_FOUND);
        }

        return Collections.unmodifiableList(hubIds);
    }
}