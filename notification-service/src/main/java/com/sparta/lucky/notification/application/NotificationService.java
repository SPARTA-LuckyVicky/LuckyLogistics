package com.sparta.lucky.notification.application;

import com.sparta.lucky.notification.application.dto.AiMessageResult;
import com.sparta.lucky.notification.application.dto.SendOrderAlertCommand;
import com.sparta.lucky.notification.application.dto.SendSlackCommand;
import com.sparta.lucky.notification.application.dto.SlackMessageResult;
import com.sparta.lucky.notification.common.exception.BusinessException;
import com.sparta.lucky.notification.common.exception.NotificationErrorCode;
import com.sparta.lucky.notification.domain.*;
import com.sparta.lucky.notification.infrastructure.client.GeminiClient;
import com.sparta.lucky.notification.infrastructure.client.OrderClient;
import com.sparta.lucky.notification.infrastructure.client.SlackClient;
import com.sparta.lucky.notification.infrastructure.client.dto.OrderResponse;
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
    private final OrderClient orderClient;
    private final SlackMessageRepository slackMessageRepository;
    private final AiMessageRepository aiMessageRepository;

    private static final UUID SYSTEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String INTERNAL_REQUEST = "true";

    // ===== 주문 알림 =====

    @Transactional
    public void sendOrderAlert(SendOrderAlertCommand request) {
        // 1. order-service에서 주문 정보 조회
        OrderResponse order = orderClient
                .getOrder(request.getOrderId(), INTERNAL_REQUEST)
                .getData();

        if (order == null) {
            throw new BusinessException(NotificationErrorCode.ORDER_NOT_FOUND);
        }

        if (order.getHubManagerSlackId() == null || order.getHubManagerSlackId().isBlank()) {
            log.warn("hubManagerSlackId가 없어 알림 발송을 건너뜁니다. orderId: {}", request.getOrderId());
            return;
        }

        log.debug("주문 알림 처리 시작 - orderId: {}", request.getOrderId());
        // 2. Gemini 프롬프트 생성
        String prompt = buildOrderAlertPrompt(request, order);

        // 3. Gemini API 호출
        String aiResponse = geminiClient.ask(prompt);

        // 4. 발송 시한 파싱
        String deadlineResult = parseDeadline(aiResponse);

        // 5. 슬랙 메시지 생성
        String slackMessage = buildSlackMessage(request, order, deadlineResult);

        // 6. 슬랙 발송
        slackClient.sendMessage(order.getHubManagerSlackId(), slackMessage);

        // 7. SlackMessage 저장
        SlackMessage slack = SlackMessage.create(
                order.getHubManagerSlackId(),
                slackMessage,
                MessageType.ORDER_NOTIFY,
                request.getOrderId(),
                SYSTEM_ID
        );
        slackMessageRepository.saveAndFlush(slack);

        // 8. AiMessage 저장
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
        if (request.getReceiverSlackId() == null || request.getReceiverSlackId().isBlank()) {
            throw new BusinessException(NotificationErrorCode.SLACK_ID_INVALID);
        }

        log.debug("슬랙 직접 발송 - receiverSlackId: {}", request.getReceiverSlackId());

        slackClient.sendMessage(request.getReceiverSlackId(), request.getMessageContent());

        SlackMessage msg = SlackMessage.create(
                request.getReceiverSlackId(),
                request.getMessageContent(),
                MessageType.SYSTEM,
                request.getRelatedOrderId(),
                request.getSenderId()
        );
        SlackMessage savedMessage = slackMessageRepository.saveAndFlush(msg);

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

    private String buildOrderAlertPrompt(SendOrderAlertCommand req, OrderResponse order) {
        int waypointCount = (req.getWaypointNames() == null) ? 0 : req.getWaypointNames().size();
        String waypoints = (req.getWaypointNames() == null || req.getWaypointNames().isEmpty())
                ? "없음" : String.join(" → ", req.getWaypointNames());

        long totalMinutes = req.getTotalDurationMinutes() != null ? req.getTotalDurationMinutes() : 0L;
        long totalDistance = req.getTotalDistanceKm() != null ? req.getTotalDistanceKm() : 0L;
        int segments = waypointCount + 1;
        long segmentMinutes = segments > 0 ? totalMinutes / segments : totalMinutes;

        return """
                당신은 물류 배차 알고리즘입니다. 아래 규칙과 예시를 보고 '최종 발송 시한'을 계산하세요.
                [규칙]
                1. 근무시간: 09:00 ~ 18:00만 이동/작업 가능
                2. 역산 중 09:00 이전이 되면 → 전날 18:00으로 이동 후 나머지 시간 계속 빼기
                3. 역산 시작점: 납기일 전날 18:00
                4. 구간당 주행시간 = 총 주행시간 ÷ (경유지 수 + 1) = %9$d ÷ %11$d = %12$d분
                5. 경유지마다 상하차 120분 소요 (최종 도착지는 상하차 없음)
                6. 역산 순서: [마지막 구간 주행] → [경유지 상하차 + 구간 주행] 반복 → [첫 구간 주행]
              
               [계산 예시 - 납기일 4월 15일, 경유지 A/B/C 3곳, 구간당 97분인 경우]
                시작: 4월 14일 18:00
                → 마지막 구간 97분: 4월 14일 16:23
                → C 상하차 120분: 4월 14일 14:23
                → C→B 구간 97분: 4월 14일 12:46
                → B 상하차 120분: 4월 14일 10:46
                → B→A 구간 97분: 4월 14일 09:09
                → A 상하차 120분: 4월 14일 07:09 → 09:00 이전! → 4월 13일 18:00으로 이동 → 남은 111분 빼기 → 4월 13일 16:09
                → 첫 구간 97분: 4월 13일 14:32
                결과: 최종 발송 시한: 04월 13일 오후 02시 32분
            
               [주문 정보]
               - 상품: %1$s %2$d개
               - 요청사항: %3$s
               - 납기일시: %4$s
               - 발송지: %5$s
               - 경유지: %6$s (총 %7$d곳)
               - 도착지: %8$s
               - 총 주행시간: %9$d분 / 총 거리: %10$dkm
               - 구간 수: %11$d / 구간당 주행시간: %12$d분
               
                위 계산 예시와 동일한 방식으로 역산하세요.
                결과는 반드시 '최종 발송 시한: MM월 DD일 오전/오후 HH시 mm분' 형식으로만 한 줄로 출력하세요.
                ⚠️ 결과가 09:00 이전이거나 18:00 이후라면 계산이 잘못된 것입니다. 다시 계산하세요.
               """.formatted(
                order.getProductName(), order.getQuantity(),
                order.getRequestNote(),
                order.getRequestedDeadline(),
                order.getOriginHubName(),
                waypoints, waypointCount,
                order.getDestinationHubName(),
                totalMinutes, totalDistance,
                segments, segmentMinutes
        );
    }

    private String parseDeadline(String aiResponse) {
        // AI 응답에서 핵심 발송 시한 추출 (응답 전체를 저장하고 일부만 파싱)
        if (aiResponse == null || aiResponse.isBlank()) return "발송 시한 계산 불가";

        // "최종 발송 시한:" 이후 텍스트 추출 시도
        String marker = "최종 발송 시한:";
        int idx = aiResponse.indexOf(marker);
        if (idx >= 0) {
            return aiResponse.substring(idx).split("\n")[0]
                    .replaceAll("\\*+", "")  // ** 제거
                    .trim();
        }
        // 파싱 실패 시 응답 전체 반환 (첫 200자)
        return "발송 시한 계산 불가";
    }

    private String buildSlackMessage(SendOrderAlertCommand req, OrderResponse order, String deadlineResult) {
        String waypoints = req.getWaypointNames() == null || req.getWaypointNames().isEmpty()
                ? "없음" : String.join(", ", req.getWaypointNames());

        return """
            📦 신규 주문 알림
            
            > 주문 번호: %s
            > 주문자 정보: %s / %s
            > 주문 시간: %s
            > 상품 정보: %s %d개
            > 요청 사항: %s
            > 납기 일시: %s
            
            🚛 배송 정보
            > 발송지: %s
            > 경유지: %s
            > 도착지: %s
            
            ⏰ %s
            """.formatted(
                req.getOrderId(),
                order.getRecipientName(), order.getRecipientSlackId(),
                order.getCreatedAt(),
                order.getProductName(), order.getQuantity(),
                order.getRequestNote(),
                order.getRequestedDeadline(),
                order.getOriginHubName(),
                waypoints,
                order.getDeliveryAddress(),
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