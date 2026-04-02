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

    // ===== 주문 알림 =====

    @Transactional
    public void sendOrderAlert(SendOrderAlertCommand request) {
        // 1. order-service에서 주문 정보 조회
        OrderResponse order = orderClient
                .getOrder(request.getOrderId(), "true")
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

        return """
                당신은 1분 단위까지 정확하게 계산하는 **물류 전문 배차 알고리즘**입니다.
                아래 규칙에 따라 '최종 발송 시한(출발 시각)'을 **역산(Backwards Calculation)** 하세요.
            
                [핵심 계산 규칙]
                1. **근무 시간**: 모든 이동과 상하차는 오직 09:00 ~ 18:00 사이에만 가능합니다.
                2. **밤샘 금지**: 역산 중 시각이 09:00 이전으로 내려가면, 전날 18:00로 이동하여 남은 시간을 뺍니다. (예: 09:00에서 1시간을 더 빼야 한다면 -> 전날 17:00가 됨)
                3. **도착 마진**: 최종 목적지 허브에는 납기일 **전날 18:00까지** 도착 완료해야 합니다.
                (납기 당일 09:00부터 업체 배송기사가 허브에서 픽업하여 수령업체로 배송합니다.)
                4. **허브 정체**: 각 허브(경유지)에 도착할 때마다 상하차 및 검수에 **120분(2시간)**이 소요됩니다.
                5. 총 주행 시간과 거리, 경유지, 경유지 개수가 제공되므로 경유지 개수에따른 상하차 시간, 주행거리, 담당자의 근무시간을반영하여 계산해주세요.
            
                
                [주문 및 경로 정보]
                - 상품: %1$s %2$d개
                - 요청사항: %3$s
                - 납기일시: %4$s
                - 발송지: %5$s
                - 경유지: %6$s (총 %7$d곳)
                - 도착지: %8$s
                - 총 주행 시간: %9$d분 / 총 거리: %10$dkm
   
                [미션]
                납기일시부터 거꾸로 계산하여, 위 모든 제약을 만족하는 '최종 출발 시각'을 산출하세요.
                결과는 반드시 '최종 발송 시한: MM월 DD일 오전/오후 HH시 mm분' 형식으로만 한 줄로 출력하세요.
                """.formatted(
                    order.getProductName(), order.getQuantity(),
                    order.getRequestNote(),
                    order.getRequestedDeadline(),
                    order.getOriginHubName(),
                    waypoints, waypointCount,
                    order.getDestinationHubName(),
                    req.getTotalDurationMinutes() != null ? req.getTotalDurationMinutes() : 0L,
                    req.getTotalDistanceKm() != null ? req.getTotalDistanceKm() : 0L
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