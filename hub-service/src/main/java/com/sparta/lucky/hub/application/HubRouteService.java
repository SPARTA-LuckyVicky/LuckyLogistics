package com.sparta.lucky.hub.application;

import com.sparta.lucky.hub.common.exception.BusinessException;
import com.sparta.lucky.hub.common.exception.HubErrorCode;
import com.sparta.lucky.hub.domain.HubRoute;
import com.sparta.lucky.hub.infrastructure.HubRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubRouteService {

    private final HubService hubService;
    private final HubRouteRepository hubRouteRepository;

    @Cacheable(cacheNames = "routes", key = "'all'")
    @Transactional(readOnly = true)
    public List<HubRoute> getHubRoutes() {
        return hubRouteRepository.findAllByDeletedAtIsNull();
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "routes", key = "'all'"),
            @CacheEvict(cacheNames = "path", allEntries = true)
    })
    @Transactional
    public void saveHubRoute(HubRoute hubRoute) {
        hubRouteRepository.save(hubRoute);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "routes", key = "'all'"),
            @CacheEvict(cacheNames = "path", allEntries = true)
    })
    @Transactional
    public HubRoute createRoute(UUID originHubId, UUID destinationHubId, int distance, int duration) {
        hubService.getHub(originHubId);
        hubService.getHub(destinationHubId);
        HubRoute route = HubRoute.create(originHubId, destinationHubId, distance, duration);
        return hubRouteRepository.save(route);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "routes", key = "'all'"),
            @CacheEvict(cacheNames = "path", allEntries = true)
    })
    @Transactional
    public HubRoute updateRoute(UUID routeId, int distance, int duration) {
        HubRoute route = findActiveRoute(routeId);
        route.updateRouteInfo(distance, duration);
        return route;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "routes", key = "'all'"),
            @CacheEvict(cacheNames = "path", allEntries = true)
    })
    @Transactional
    public void deleteRoute(UUID routeId, UUID deletedBy) {
        HubRoute route = findActiveRoute(routeId);
        route.softDelete(deletedBy);
    }

    private HubRoute findActiveRoute(UUID routeId) {
        return hubRouteRepository.findByIdAndDeletedAtIsNull(routeId)
                .orElseThrow(() -> new BusinessException(HubErrorCode.HUB_ROUTE_NOT_FOUND));
    }
}