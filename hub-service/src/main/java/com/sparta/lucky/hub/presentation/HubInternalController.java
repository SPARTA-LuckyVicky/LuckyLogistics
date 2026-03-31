package com.sparta.lucky.hub.presentation;

import com.sparta.lucky.hub.application.HubService;
import com.sparta.lucky.hub.common.response.ApiResponse;
import com.sparta.lucky.hub.presentation.dto.GetHubResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/hubs")
@RequiredArgsConstructor
public class HubInternalController {

    private final HubService hubService;

    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<GetHubResDto>> getHub(@PathVariable UUID hubId) {
        return ResponseEntity.ok(ApiResponse.success(GetHubResDto.from(hubService.getHub(hubId))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetHubResDto>>> getHubs(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(hubService.getHubs(pageable).map(GetHubResDto::from)));
    }
}