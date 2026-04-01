package com.sparta.lucky.hub.presentation;

import com.sparta.lucky.hub.application.RouteService;
import com.sparta.lucky.hub.application.dto.GetRouteResult;
import com.sparta.lucky.hub.presentation.dto.GetRouteResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/internal/api/v1/routes")
@RequiredArgsConstructor
public class RouteInternalController {

    private final RouteService routeService;

    @GetMapping
    public GetRouteResDto getRoute(
            @RequestParam UUID originHubId,
            @RequestParam BigDecimal destinationLat,
            @RequestParam BigDecimal destinationLong
    ) {
        GetRouteResult result = routeService.getRoute(originHubId, destinationLat, destinationLong);

        return GetRouteResDto.from(result);
    }
}
