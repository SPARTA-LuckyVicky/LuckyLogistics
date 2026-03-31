package com.sparta.lucky.hub.presentation;

import com.sparta.lucky.hub.application.HubService;
import com.sparta.lucky.hub.application.dto.*;
import com.sparta.lucky.hub.common.response.ApiResponse;
import com.sparta.lucky.hub.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    // Todo: 권한 설정
    @PostMapping
    public ResponseEntity<ApiResponse<PostHubResDto>> createHub(@Valid @RequestBody PostHubReqDto request) {
        CreateHubCommand command = CreateHubCommand.of(
                request.getName(), request.getAddress(), request.getLatitude(), request.getLongitude()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(PostHubResDto.from(hubService.createHub(command))));
    }

    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<GetHubResDto>> getHub(@PathVariable UUID hubId) {
        return ResponseEntity.ok(ApiResponse.success(GetHubResDto.from(hubService.getHub(hubId))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetHubResDto>>> getHubs(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(hubService.getHubs(pageable).map(GetHubResDto::from)));
    }

    @PatchMapping("/{hubId}")
    public ResponseEntity<ApiResponse<GetHubResDto>> updateHub(
            @PathVariable UUID hubId,
            @Valid @RequestBody PatchHubReqDto request
    ) {
        UpdateHubCommand command = UpdateHubCommand.of(
                hubId, request.getName(), request.getAddress(), request.getLatitude(), request.getLongitude()
        );
        return ResponseEntity.ok(ApiResponse.success(GetHubResDto.from(hubService.updateHub(command))));
    }

    @PatchMapping("/{hubId}/manager")
    public ResponseEntity<ApiResponse<Void>> assignManager(
            @PathVariable UUID hubId,
            @Valid @RequestBody PatchHubManagerReqDto request
    ) {
        hubService.assignManager(AssignManagerCommand.of(hubId, request.getManagerId()));
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/{hubId}")
    public ResponseEntity<ApiResponse<Void>> deleteHub(
            @PathVariable UUID hubId,
            @RequestHeader("X-User-Id") UUID deletedBy // Todo: 추후 수정
    ) {
        hubService.deleteHub(hubId, deletedBy);
        return ResponseEntity.noContent().build();
    }
}