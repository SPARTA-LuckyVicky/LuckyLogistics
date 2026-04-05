package com.sparta.lucky.deliveryservice.infrastructure.jpa;

import com.sparta.lucky.deliveryservice.domain.delivery.DeliveryRoute;
import com.sparta.lucky.deliveryservice.domain.repos.DeliveryRouteRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaDeliveryRouteRepository extends JpaRepository<DeliveryRoute, UUID>,
    DeliveryRouteRepository {

}
