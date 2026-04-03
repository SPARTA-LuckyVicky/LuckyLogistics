package com.sparta.lucky.product.infrastructure.feign.dto;

// { "success": true, "data": {...} } 형태 응답 역직렬화 래퍼

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

@Getter
@NoArgsConstructor
public class FeignApiResponse<T> {
    private boolean success;
    private T data;

    /**
     * success=false 이거나 data=null인 경우 호출자가 지정한 예외를 던지는 안전 접근 메서드.
     * getData()를 직접 사용하면 success 필드를 무시하게 되어 실패 응답이 NPE로 전파될 수 있음.
     */
    public T requireData(Supplier<? extends RuntimeException> exceptionSupplier) {
        if (!success || data == null) {
            throw exceptionSupplier.get();
        }
        return data;
    }
}
