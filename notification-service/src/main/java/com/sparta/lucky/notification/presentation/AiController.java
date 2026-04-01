package com.sparta.lucky.notification.presentation;

import com.sparta.lucky.notification.application.NotificationService;
import com.sparta.lucky.notification.common.response.ApiResponse;
import com.sparta.lucky.notification.presentation.dto.GetAiResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "AI", description = "AI 메시지 관리 API")
@RestController
@RequestMapping("/api/v1/notifications/ai")
@RequiredArgsConstructor
public class AiController {

    private final NotificationService notificationService;

    @Operation(summary = "AI 메시지 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetAiResDto>>> getAiMessages(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService.getAiMessages(pageable)
                                .map(GetAiResDto::from)
                )
        );
    }

    @Operation(summary = "AI 메시지 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetAiResDto>> getAiMessage(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(GetAiResDto.from(notificationService.getAiMessage(id)))
        );
    }
}