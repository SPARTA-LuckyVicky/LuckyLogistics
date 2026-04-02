package com.sparta.lucky.notification.infrastructure;

import com.sparta.lucky.notification.common.exception.BusinessException;
import com.sparta.lucky.notification.common.exception.NotificationErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=";

    private final RestTemplate restTemplate;

    public String ask(String prompt) {
        log.debug("Gemini API 요청 시작");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gemini REST API 요청 형식
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    GEMINI_URL + apiKey, request, Map.class
            );
            if (response.getBody() == null) {
                throw new BusinessException(NotificationErrorCode.GEMINI_API_FAILED);
            }

            List<Map> candidates = (List<Map>) response.getBody().get("candidates");
            // null 또는 빈 배열 → safety filter 등으로 응답 차단된 경우
            if (candidates == null || candidates.isEmpty()) {
                log.warn("Gemini API 응답에 candidates가 없습니다. safety filter에 의해 차단되었을 수 있습니다.");
                throw new BusinessException(NotificationErrorCode.GEMINI_API_FAILED);
            }

            Map content = (Map) candidates.get(0).get("content");
            if (content == null) {
                log.warn("Gemini API 응답 content가 null입니다.");
                throw new BusinessException(NotificationErrorCode.GEMINI_API_FAILED);
            }

            List<Map> parts = (List<Map>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                log.warn("Gemini API 응답 parts가 없습니다.");
                throw new BusinessException(NotificationErrorCode.GEMINI_API_FAILED);
            }

            String result = (String) parts.get(0).get("text");
            if (result == null || result.isBlank()) {
                log.warn("Gemini API 응답 text가 비어있습니다.");
                throw new BusinessException(NotificationErrorCode.GEMINI_API_FAILED);
            }

            log.debug("Gemini API 응답 수신 완료");
            return result;

        } catch (BusinessException e) {
            throw e;  // ← 먼저 잡아서 그대로 던지기
        } catch (Exception e) {
            log.warn("Gemini API 호출 실패", e);
            throw new BusinessException(NotificationErrorCode.GEMINI_API_FAILED);
        }
    }


}
