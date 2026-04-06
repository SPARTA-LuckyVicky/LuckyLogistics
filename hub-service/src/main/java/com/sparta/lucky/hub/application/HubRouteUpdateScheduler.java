package com.sparta.lucky.hub.application;

import com.sparta.lucky.hub.application.dto.GetHubResult;
import com.sparta.lucky.hub.domain.HubRoute;
import com.sparta.lucky.hub.infrastructure.tmap.TmapRouteClient;
import com.sparta.lucky.hub.infrastructure.tmap.TmapRouteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubRouteUpdateScheduler {

    private final HubRouteService hubRouteService;
    private final HubService hubService;
    private final TmapRouteClient tmapRouteClient;

    @Scheduled(initialDelay = 0, fixedRate = 30 * 60 * 1000)
    public void updateHubRouteInfo() {
        log.info("[HubRouteUpdateScheduler] 허브 경로 distance/duration 업데이트 시작");

        List<HubRoute> routes = hubRouteService.getHubRoutes();
        Map<UUID, GetHubResult> hubMap = hubService.getHubs().stream()
                .collect(Collectors.toMap(GetHubResult::getId, hub -> hub));

        int successCount = 0;
        int failCount = 0;

        for (HubRoute route : routes) {
            GetHubResult origin = hubMap.get(route.getOriginHubId());
            GetHubResult destination = hubMap.get(route.getDestinationHubId());

            if (origin == null || destination == null) {
                log.warn("[HubRouteUpdateScheduler] 허브 정보를 찾을 수 없음 - routeId: {}", route.getId());
                failCount++;
                continue;
            }

            try {
                TmapRouteResponse response = tmapRouteClient.getRoute(
                        origin.getLongitude(), origin.getLatitude(),
                        destination.getLongitude(), destination.getLatitude()
                );

                route.updateRouteInfo(response.getTotalDistance(), response.getTotalTime());
                hubRouteService.saveHubRoute(route);
                successCount++;

                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("[HubRouteUpdateScheduler] 스레드 인터럽트 발생", e);
                break;
            } catch (Exception e) {
                log.error("[HubRouteUpdateScheduler] 경로 업데이트 실패 - routeId: {}, origin: {}, destination: {}",
                        route.getId(), origin.getName(), destination.getName(), e);
                failCount++;
            }
        }

        log.info("[HubRouteUpdateScheduler] 업데이트 완료 - 성공: {}, 실패: {}", successCount, failCount);
    }
}