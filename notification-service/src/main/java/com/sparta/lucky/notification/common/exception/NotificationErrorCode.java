package com.sparta.lucky.notification.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    SLACK_MESSAGE_NOT_FOUND("NTF_001", "존재하지 않는 슬랙 메시지입니다.", 404),
    AI_MESSAGE_NOT_FOUND("NTF_002", "존재하지 않는 AI 메시지입니다.", 404),
    SLACK_SEND_FAILED("NTF_003", "슬랙 메시지 발송에 실패했습니다.", 500),
    GEMINI_API_FAILED("NTF_004", "AI 응답 생성에 실패했습니다.", 500),
    SLACK_ID_INVALID("NTF_005", "슬랙 ID 검증에 실패했습니다.", 404);

    private final String code;
    private final String message;
    private final int httpStatus;
}
