package com.sparta.lucky.notification.presentation;

import com.sparta.lucky.notification.application.NotificationService;
import com.sparta.lucky.notification.application.dto.SlackMessageResult;
import com.sparta.lucky.notification.common.response.ApiResponse;
import com.sparta.lucky.notification.domain.MessageType;
import com.sparta.lucky.notification.presentation.dto.GetSlackResDto;
import com.sparta.lucky.notification.presentation.dto.PostSlackReqDto;
import com.sparta.lucky.notification.presentation.dto.PostSlackResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Slack", description = "슬랙 메시지 관리 API")
@RestController
@RequestMapping("/api/v1/notifications/slack")
@RequiredArgsConstructor
public class SlackController {

    private final NotificationService notificationService;

    @Operation(summary = "슬랙 메시지 발송")
    @PostMapping
    public ResponseEntity<ApiResponse<PostSlackResDto>> sendSlack(
            @RequestBody @Valid PostSlackReqDto request,
            @RequestHeader(value = "X-User-Id", required = false) UUID senderId
    ) {
        SlackMessageResult result = notificationService.sendSlack(
                request.toCommand(senderId)
        );
        return ResponseEntity.ok(ApiResponse.success(PostSlackResDto.from(result)));
    }

    @Operation(summary = "슬랙 메시지 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetSlackResDto>>> getSlackMessages(
            @RequestParam(required = false) MessageType messageType,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService.getSlackMessages(messageType, pageable)
                                .map(GetSlackResDto::from)
                )
        );
    }

    @Operation(summary = "슬랙 메시지 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetSlackResDto>> getSlackMessage(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(GetSlackResDto.from(notificationService.getSlackMessage(id)))
        );
    }

    @Operation(summary = "슬랙 메시지 삭제", description = "Soft delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSlackMessage(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id") UUID deletedBy
    ) {
        notificationService.deleteSlackMessage(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success());
    }
}