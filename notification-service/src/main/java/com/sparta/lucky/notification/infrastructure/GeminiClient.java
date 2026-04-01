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
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

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
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            String result = (String) parts.get(0).get("text");

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
