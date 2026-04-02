package com.sparta.lucky.user.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String code;
    private final String message;

    // 성공 (데이터 있음)
    private ApiResponse(T data) {
        this.success = true;
        this.data = data;
        this.code = null;
        this.message = null;
    }

    // 성공 (데이터 없음, 204 no-content 용)
    private ApiResponse() {
        this.success = true;
        this.data = null;
        this.code = null;
        this.message = null;
    }

    // 실패
    private ApiResponse(String code, String message) {
        this.success = false;
        this.data = null;
        this.code = code;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>();
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message);
    }
}