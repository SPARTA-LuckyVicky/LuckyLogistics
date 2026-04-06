package com.sparta.lucky.hub.infrastructure.tmap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class TmapRouteClient {

    private static final String TMAP_BASE_URL = "https://apis.openapi.sk.com";
    private static final String ROUTE_PATH = "/tmap/routes";

    @Value("${tmap.api-key}")
    private String apiKey;

    public TmapRouteResponse getRoute(BigDecimal startLongitude, BigDecimal startLatitude,
                                      BigDecimal endLongitude, BigDecimal endLatitude) {
        Map<String, String> requestBody = Map.of(
                "startX", startLongitude.toPlainString(),
                "startY", startLatitude.toPlainString(),
                "endX", endLongitude.toPlainString(),
                "endY", endLatitude.toPlainString(),
                "searchOption", "0"
        );

        return RestClient.create(TMAP_BASE_URL)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(ROUTE_PATH)
                        .queryParam("version", "1")
                        .build())
                .header("appKey", apiKey)
                .body(requestBody)
                .retrieve()
                .body(TmapRouteResponse.class);
    }
}