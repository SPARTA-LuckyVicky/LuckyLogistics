package com.sparta.lucky.product.infrastructure.feign.dto;

// { "success": true, "data": {...} } 형태 응답 역직렬화 래퍼

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeignApiResponse<T> {
    private boolean success;
    private T data;
}
