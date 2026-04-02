package com.sparta.lucky.deliveryservice.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonApiResponse<T>(
    String code,
    String message,
    T data
    ) {
    // on success
    public static <T> CommonApiResponse<T> success(ResponseCode code) {
        return new CommonApiResponse<>(code.code(), code.message(), null);
    }

    public static <T> CommonApiResponse<T> success(ResponseCode code, T data) {
        return new CommonApiResponse<>(code.code(), code.message(), data);
    }

    // on error
    public static <T> CommonApiResponse<T> error(ResponseCode code) {
        return new CommonApiResponse<>(code.code(), code.message(), null);
    }

    public static <T> CommonApiResponse<T> error(ResponseCode code, T data) {
        return new CommonApiResponse<>(code.code(), code.message(), data);
    }
}
