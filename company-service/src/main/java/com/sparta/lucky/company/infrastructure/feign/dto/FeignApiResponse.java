package com.sparta.lucky.company.infrastructure.feign.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

// { "success": true, "data": {...} } 형태 응답 역직렬화 래퍼
@Getter
@NoArgsConstructor
public class FeignApiResponse<T> {
    private boolean success;
    private T data;

    // success=false 이거나 data=null인 경우 지정한 예외를 던지는 안전 접근 메서드
    public T requireData(Supplier<? extends RuntimeException> exceptionSupplier) {
        if (!success || data == null) {
            throw exceptionSupplier.get();
        }
        return data;
    }
}