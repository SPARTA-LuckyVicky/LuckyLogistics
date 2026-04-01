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
        return new CommonApiResponse<T>(ResponseCode.OK.code(), ResponseCode.OK.message(), null);
    }

    public static <T> CommonApiResponse<T> success(T data) {
        return new CommonApiResponse<>(ResponseCode.OK.code(), ResponseCode.OK.message(), data);
    }

    // on success - no contents
    public static <T> CommonApiResponse<T> noContent() {
        return new CommonApiResponse<>(ResponseCode.OK.code(), ResponseCode.OK.message(), null);
    }

    public static <T> CommonApiResponse<T> noContent(ResponseCode code) {
        return new CommonApiResponse<>(code.code(), code.message(), null);
    }

    // on error
    public static <T> CommonApiResponse<T> error(ResponseCode code) {
        return new CommonApiResponse<>(code.code(), code.message(), null);
    }

    public static <T> CommonApiResponse<T> error(ResponseCode code, T data) {
        return new CommonApiResponse<>(code.code(), code.message(), data);
    }
}
