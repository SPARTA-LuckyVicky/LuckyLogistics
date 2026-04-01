package com.sparta.lucky.hub.presentation;

import com.sparta.lucky.hub.application.HubService;
import com.sparta.lucky.hub.application.dto.AssignManagerCommand;
import com.sparta.lucky.hub.common.response.ApiResponse;
import com.sparta.lucky.hub.presentation.dto.GetHubResDto;
import com.sparta.lucky.hub.presentation.dto.PatchHubManagerReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Hub Internal", description = "허브 내부 서비스 간 통신 API")
@RestController
@RequestMapping("/internal/hubs")
@RequiredArgsConstructor
public class HubInternalController {

    private final HubService hubService;

    @Operation(summary = "[Internal] 허브 단건 조회", description = "서비스 내부에서 허브 ID로 허브 정보를 조회합니다.")
    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<GetHubResDto>> getHub(
            @Parameter(description = "허브 ID") @PathVariable UUID hubId
    ) {
        return ResponseEntity.ok(ApiResponse.success(GetHubResDto.from(hubService.getHub(hubId))));
    }

    @Operation(summary = "[Internal] 허브 목록 조회", description = "서비스 내부에서 허브 목록을 페이지 단위로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetHubResDto>>> getHubs(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(hubService.getHubs(pageable).map(GetHubResDto::from)));
    }

    @Operation(summary = "[Internal] 허브 매니저 배정", description = "허브에 매니저를 배정합니다.")
    @PatchMapping("/{hubId}/manager")
    public ResponseEntity<ApiResponse<Void>> assignManager(
            @Parameter(description = "허브 ID") @PathVariable UUID hubId,
            @Valid @RequestBody PatchHubManagerReqDto request
    ) {
        hubService.assignManager(AssignManagerCommand.of(hubId, request.getManagerId()));
        return ResponseEntity.ok(ApiResponse.success());
    }
}