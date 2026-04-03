package com.sparta.lucky.hub.presentation;

import com.sparta.lucky.hub.application.RouteService;
import com.sparta.lucky.hub.common.response.ApiResponse;
import com.sparta.lucky.hub.presentation.dto.PatchRouteReqDto;
import com.sparta.lucky.hub.presentation.dto.PostRouteReqDto;
import com.sparta.lucky.hub.presentation.dto.RouteResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Route", description = "허브 경로 관리 API")
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @Operation(summary = "허브 경로 생성", description = "두 허브 간 경로를 생성합니다. (MASTER 전용)")
    @PreAuthorize("hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<ApiResponse<RouteResDto>> createRoute(
            @Valid @RequestBody PostRouteReqDto request
    ) {
        RouteResDto response = RouteResDto.from(
                routeService.createRoute(
                        request.getOriginHubId(),
                        request.getDestinationHubId(),
                        request.getDistance(),
                        request.getDuration()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "허브 경로 수정", description = "경로의 거리 및 소요 시간을 수정합니다. (MASTER 전용)")
    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/{routeId}")
    public ResponseEntity<ApiResponse<RouteResDto>> updateRoute(
            @Parameter(description = "경로 ID") @PathVariable UUID routeId,
            @Valid @RequestBody PatchRouteReqDto request
    ) {
        RouteResDto response = RouteResDto.from(
                routeService.updateRoute(routeId, request.getDistance(), request.getDuration())
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "허브 경로 삭제", description = "경로를 소프트 삭제합니다. (MASTER 전용)")
    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/{routeId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(
            @Parameter(description = "경로 ID") @PathVariable UUID routeId,
            @Parameter(hidden = true) @AuthenticationPrincipal String deletedBy
    ) {
        routeService.deleteRoute(routeId, UUID.fromString(deletedBy));
        return ResponseEntity.noContent().build();
    }
}