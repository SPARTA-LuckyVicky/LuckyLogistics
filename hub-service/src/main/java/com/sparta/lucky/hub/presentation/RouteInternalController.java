package com.sparta.lucky.hub.presentation;

import com.sparta.lucky.hub.application.RouteService;
import com.sparta.lucky.hub.application.dto.GetRouteResult;
import com.sparta.lucky.hub.presentation.dto.GetRouteResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Route Internal", description = "허브 경로 내부 서비스 간 통신 API")
@RestController
@RequestMapping("/internal/api/v1/routes")
@RequiredArgsConstructor
public class RouteInternalController {

    private final RouteService routeService;

    @Operation(summary = "[Internal] 허브 최단 경로 조회", description = "허브 간 최단 경로를 조회합니다.")
    @GetMapping
    public GetRouteResDto getRoute(
            @Parameter(description = "출발 허브 ID") @RequestParam @NotNull UUID originHubId,
            @Parameter(description = "도착 위도") @RequestParam @NotNull BigDecimal destinationLat,
            @Parameter(description = "도착 경도") @RequestParam @NotNull BigDecimal destinationLong
    ) {
        GetRouteResult result = routeService.getRoute(originHubId, destinationLat, destinationLong);

        return GetRouteResDto.from(result);
    }
}
