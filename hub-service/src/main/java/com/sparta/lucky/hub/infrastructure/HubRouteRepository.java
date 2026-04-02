package com.sparta.lucky.hub.infrastructure;

import com.sparta.lucky.hub.domain.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {

    List<HubRoute> findAllByDeletedAtIsNull();
}