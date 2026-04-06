package com.sparta.lucky.hub.application;

import com.sparta.lucky.hub.application.dto.GetRouteResult;
import com.sparta.lucky.hub.application.dto.GetRouteResult.RouteSegment;
import com.sparta.lucky.hub.common.exception.BusinessException;
import com.sparta.lucky.hub.common.exception.HubErrorCode;
import com.sparta.lucky.hub.domain.HubRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubPathService {

    private final HubRouteService hubRouteService;
    private final HubPathCacheService hubPathCacheService;

    @Transactional(readOnly = true)
    public GetRouteResult getRoute(UUID originHubId, UUID destinationHubId) {

        // 출발 허브 == 도착 허브인 경우
        if (originHubId.equals(destinationHubId)) {
            return GetRouteResult.of(originHubId, destinationHubId, 0, 0, List.of());
        }

        // 1) path 캐시: [서울, 대전, 부산, ...] 순서의 허브 ID 목록
        List<UUID> hubIds = hubPathCacheService.getPathHubIds(originHubId, destinationHubId);

        // 2) routes 캐시: 현재 시간/거리 정보를 갖는 전체 노선 (양방향 조회용 맵)
        Map<String, HubRoute> routeMap = buildRouteMap(hubRouteService.getHubRoutes());

        // 3) 연속된 허브 쌍으로 RouteSegment 조립: (서울→대전), (대전→부산), ...
        List<RouteSegment> segments = new ArrayList<>();
        int totalDuration = 0;
        int totalDistance = 0;

        for (int i = 0; i < hubIds.size() - 1; i++) {
            UUID from = hubIds.get(i);
            UUID to = hubIds.get(i + 1);
            HubRoute route = routeMap.get(routeKey(from, to));
            if (route == null) {
                throw new BusinessException(HubErrorCode.HUB_ROUTE_NOT_FOUND);
            }
            segments.add(new RouteSegment(from, to, route.getDuration(), route.getDistance()));
            totalDuration += route.getDuration();
            totalDistance += route.getDistance();
        }

        return GetRouteResult.of(originHubId, destinationHubId, totalDuration, totalDistance, segments);
    }

    /** 양방향 조회를 위해 A→B, B→A 모두 등록 */
    private Map<String, HubRoute> buildRouteMap(List<HubRoute> routes) {
        Map<String, HubRoute> map = new HashMap<>(routes.size() * 2);
        for (HubRoute route : routes) {
            map.put(routeKey(route.getOriginHubId(), route.getDestinationHubId()), route);
            map.put(routeKey(route.getDestinationHubId(), route.getOriginHubId()), route);
        }
        return map;
    }

    private String routeKey(UUID from, UUID to) {
        return from + "-" + to;
    }
}