package com.sparta.lucky.notification.infrastructure;

import com.sparta.lucky.notification.common.exception.BusinessException;
import com.sparta.lucky.notification.common.exception.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackClient {

    @Value("${slack.token}")
    private String slackToken;

    private static final String SLACK_API_URL = "https://slack.com/api/chat.postMessage";

    private final RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    public void sendMessage(String receiverSlackId, String message) {
        log.debug("슬랙 메시지 발송 시작 - receiverSlackId: {}", maskSlackId(receiverSlackId));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(slackToken);

        Map<String, String> body = Map.of(
                "channel", receiverSlackId,
                "text", message
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    SLACK_API_URL,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            if (response.getBody() == null) {
                throw new BusinessException(NotificationErrorCode.SLACK_SEND_FAILED);
            }
            // Slack API는 항상 200을 반환하고
            // 성공 여부는 body의 "ok" 필드로 확인해야 함
            // { "ok": true } or { "ok": false, "error": "channel_not_found" }
            Boolean ok = (Boolean) response.getBody().get("ok");
            if (!Boolean.TRUE.equals(ok)) {
                String error = (String) response.getBody().get("error");
                log.warn("슬랙 발송 실패 - receiverSlackId: {}, error: {}", maskSlackId(receiverSlackId), error);
                throw new BusinessException(NotificationErrorCode.SLACK_SEND_FAILED);
            }

            log.debug("슬랙 메시지 발송 성공 - receiverSlackId: {}", maskSlackId(receiverSlackId));

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("슬랙 발송 중 예외 발생 - receiverSlackId: {}", receiverSlackId, e);
            throw new BusinessException(NotificationErrorCode.SLACK_SEND_FAILED);
        }
    }

    private String maskSlackId(String slackId) {
        if (slackId == null || slackId.length() <= 4) return "****";
        return slackId.substring(0, 4) + "****";
    }
}