package com.sparta.lucky.notification.application;

import com.sparta.lucky.notification.application.dto.AiMessageResult;
import com.sparta.lucky.notification.application.dto.SendOrderAlertCommand;
import com.sparta.lucky.notification.application.dto.SendSlackCommand;
import com.sparta.lucky.notification.application.dto.SlackMessageResult;
import com.sparta.lucky.notification.common.exception.BusinessException;
import com.sparta.lucky.notification.common.exception.NotificationErrorCode;
import com.sparta.lucky.notification.domain.*;
import com.sparta.lucky.notification.infrastructure.GeminiClient;
import com.sparta.lucky.notification.infrastructure.SlackClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final SlackClient slackClient;
    private final GeminiClient geminiClient;
    private final SlackMessageRepository slackMessageRepository;
    private final AiMessageRepository aiMessageRepository;

    // ===== 주문 알림 =====

    @Transactional
    public void sendOrderAlert(SendOrderAlertCommand request) {
        log.debug("주문 알림 처리 시작 - orderId: {}", request.getOrderId());

        // 1. Gemini 프롬프트 생성
        String prompt = buildOrderAlertPrompt(request);

        // 2. Gemini API 호출
        String aiResponse = geminiClient.ask(prompt);
        log.debug("Gemini 응답 수신 - orderId: {}", request.getOrderId());

        // 3. 발송 시한 파싱 (AI 응답 전체를 deadlineResult로 저장)
        String deadlineResult = parseDeadline(aiResponse);

        // 4. 슬랙 메시지 내용 생성
        String slackMessage = buildSlackMessage(request, deadlineResult);

        // 5. 슬랙 발송
        slackClient.sendMessage(request.getHubManagerSlackId(), slackMessage);

        // 6. SlackMessage 저장
        SlackMessage slack = SlackMessage.create(
                request.getHubManagerSlackId(),
                slackMessage,
                MessageType.ORDER_NOTIFY,
                request.getOrderId(),
                null  // 내부 시스템 발송
        );
        slackMessageRepository.save(slack);

        // 7. AiMessage 저장
        AiMessage ai = AiMessage.create(
                request.getOrderId(),
                prompt,
                aiResponse,
                deadlineResult,
                slack.getId()
        );
        aiMessageRepository.save(ai);

        log.debug("주문 알림 처리 완료 - orderId: {}", request.getOrderId());
    }

    // ===== 슬랙 직접 발송 =====

    @Transactional
    public SlackMessageResult sendSlack(SendSlackCommand request) {
        log.debug("슬랙 직접 발송 - receiverSlackId: {}", request.getReceiverSlackId());

        slackClient.sendMessage(request.getReceiverSlackId(), request.getMessageContent());

        SlackMessage msg = SlackMessage.create(
                request.getReceiverSlackId(),
                request.getMessageContent(),
                MessageType.SYSTEM,
                request.getRelatedOrderId(),
                request.getSenderId()
        );
        slackMessageRepository.save(msg);

        return SlackMessageResult.from(msg);
    }

    // ===== 슬랙 메시지 조회 =====

    public Page<SlackMessageResult> getSlackMessages(MessageType messageType, Pageable pageable) {
        pageable = validatePageable(pageable);
        if (messageType != null) {
            return slackMessageRepository.findByMessageType(messageType, pageable)
                    .map(SlackMessageResult::from);
        }
        return slackMessageRepository.findAll(pageable)
                .map(SlackMessageResult::from);
    }

    public SlackMessageResult getSlackMessage(UUID id) {
        return SlackMessageResult.from(findSlackMessageById(id));
    }

    // ===== 슬랙 메시지 삭제 =====

    @Transactional
    public void deleteSlackMessage(UUID id, UUID deletedBy) {
        log.debug("슬랙 메시지 삭제 - id: {}", id);
        SlackMessage msg = findSlackMessageById(id);
        msg.softDelete(deletedBy);
    }

    // ===== AI 메시지 조회 =====

    public Page<AiMessageResult> getAiMessages(Pageable pageable) {
        pageable = validatePageable(pageable);
        return aiMessageRepository.findAll(pageable)
                .map(AiMessageResult::from);
    }

    public AiMessageResult getAiMessage(UUID id) {
        return AiMessageResult.from(findAiMessageById(id));
    }

    // ===== 내부 메서드 =====

    private String buildOrderAlertPrompt(SendOrderAlertCommand req) {
        String waypoints = req.getWaypointNames() == null || req.getWaypointNames().isEmpty()
                ? "없음"
                : String.join(" → ", req.getWaypointNames());

        return """
        아래 주문 정보를 바탕으로 최종 발송 시한을 계산해주세요.
        배송담당자 근무시간: 09:00 ~ 18:00
        
        [주문 정보]
        - 상품: %s %d개
        - 요청사항: %s
        - 납기일시: %s
        
        [배송 경로]
        - 발송지: %s
        - 경유지: %s
        - 도착지: %s
        - 총 예상 소요시간: %d분
        - 총 거리: %dkm
        
        최종 발송 시한만 간결하게 알려주세요.
        예) 최종 발송 시한: 4월 5일 오전 9시
        """.formatted(
                req.getProductName(), req.getQuantity(),
                req.getRequestNote(),
                req.getRequestedDeadline(),
                req.getOriginHubName(),
                waypoints,
                req.getDestinationHubName(),
                req.getTotalDurationMinutes(),
                req.getTotalDistanceKm()
        );
    }

    private String parseDeadline(String aiResponse) {
        // AI 응답에서 핵심 발송 시한 추출 (응답 전체를 저장하고 일부만 파싱)
        if (aiResponse == null || aiResponse.isBlank()) return "발송 시한 계산 불가";

        // "최종 발송 시한:" 이후 텍스트 추출 시도
        String marker = "최종 발송 시한:";
        int idx = aiResponse.indexOf(marker);
        if (idx >= 0) {
            return aiResponse.substring(idx).split("\n")[0].trim();
        }
        // 파싱 실패 시 응답 전체 반환 (첫 200자)
        return aiResponse.length() > 200 ? aiResponse.substring(0, 200) : aiResponse;
    }

    private String buildSlackMessage(SendOrderAlertCommand req, String deadlineResult) {
        String waypoints = req.getWaypointNames() == null || req.getWaypointNames().isEmpty()
                ? "없음"
                : String.join(", ", req.getWaypointNames());

        return """
            📦 신규 주문 알림
            
            주문 번호: %s
            주문자 정보: %s / %s
            주문 시간: %s
            상품 정보: %s %d개
            요청 사항: %s
            발송지: %s
            경유지: %s
            도착지: %s
            
            ⏰ %s
            """.formatted(
                req.getOrderId(),
                req.getRecipientName(), req.getRecipientSlackId(),
                req.getOrderedAt(),
                req.getProductName(), req.getQuantity(),
                req.getRequestNote(),
                req.getOriginHubName(),
                waypoints,
                req.getDeliveryAddress(),
                deadlineResult
        );
    }

    private SlackMessage findSlackMessageById(UUID id) {
        return slackMessageRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("슬랙 메시지 조회 실패 - id: {}", id);
                    return new BusinessException(NotificationErrorCode.SLACK_MESSAGE_NOT_FOUND);
                });
    }

    private AiMessage findAiMessageById(UUID id) {
        return aiMessageRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("AI 메시지 조회 실패 - id: {}", id);
                    return new BusinessException(NotificationErrorCode.AI_MESSAGE_NOT_FOUND);
                });
    }

    private Pageable validatePageable(Pageable pageable) {
        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            return PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }
        return pageable;
    }
}