package com.sparta.lucky.hub.presentation;

import com.sparta.lucky.hub.application.HubService;
import com.sparta.lucky.hub.application.dto.*;
import com.sparta.lucky.hub.common.response.ApiResponse;
import com.sparta.lucky.hub.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Hub", description = "허브 관리 API")
@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    // Todo: 권한 설정
    @Operation(summary = "허브 생성", description = "새로운 허브를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PostHubResDto>> createHub(@Valid @RequestBody PostHubReqDto request) {
        CreateHubCommand command = CreateHubCommand.of(
                request.getName(), request.getAddress(), request.getLatitude(), request.getLongitude()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(PostHubResDto.from(hubService.createHub(command))));
    }

    @Operation(summary = "허브 단건 조회", description = "허브 ID로 허브 정보를 조회합니다.")
    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<GetHubResDto>> getHub(
            @Parameter(description = "허브 ID") @PathVariable UUID hubId
    ) {
        return ResponseEntity.ok(ApiResponse.success(GetHubResDto.from(hubService.getHub(hubId))));
    }

    @Operation(summary = "허브 목록 조회", description = "허브 목록을 페이지 단위로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetHubResDto>>> getHubs(@ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(hubService.getHubsByPage(pageable).map(GetHubResDto::from)));
    }

    @Operation(summary = "허브 정보 수정", description = "허브의 이름, 주소, 위경도를 수정합니다.")
    @PatchMapping("/{hubId}")
    public ResponseEntity<ApiResponse<GetHubResDto>> updateHub(
            @Parameter(description = "허브 ID") @PathVariable UUID hubId,
            @Valid @RequestBody PatchHubReqDto request
    ) {
        UpdateHubCommand command = UpdateHubCommand.of(
                hubId, request.getName(), request.getAddress(), request.getLatitude(), request.getLongitude()
        );
        return ResponseEntity.ok(ApiResponse.success(GetHubResDto.from(hubService.updateHub(command))));
    }

    @Operation(summary = "허브 삭제", description = "허브를 소프트 삭제합니다.")
    @DeleteMapping("/{hubId}")
    public ResponseEntity<ApiResponse<Void>> deleteHub(
            @Parameter(description = "허브 ID") @PathVariable UUID hubId,
            @Parameter(description = "요청자 ID", hidden = true) @RequestHeader("X-User-Id") UUID deletedBy // Todo: 추후 수정
    ) {
        hubService.deleteHub(hubId, deletedBy);
        return ResponseEntity.noContent().build();
    }
}