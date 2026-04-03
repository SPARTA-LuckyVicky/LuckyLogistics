package com.sparta.lucky.order.infrastructure.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeignApiResponse<T> {
    private boolean success;
    private T data;
    private String code;
    private String message;
}
