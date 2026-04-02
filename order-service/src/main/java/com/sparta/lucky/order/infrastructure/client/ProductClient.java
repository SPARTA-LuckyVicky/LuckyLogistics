package com.sparta.lucky.order.infrastructure.client;

import com.sparta.lucky.order.infrastructure.client.dto.ApiResponse;
import com.sparta.lucky.order.infrastructure.client.dto.ProductResponse;
import com.sparta.lucky.order.infrastructure.client.dto.StockResponse;
import com.sparta.lucky.order.infrastructure.client.dto.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name="product-service")
public interface ProductClient {

    @GetMapping("/internal/api/v1/products/{productId}")
    ApiResponse<ProductResponse> getProduct(@PathVariable UUID productId);

    @PatchMapping("/internal/api/v1/products/{productId}/stock/decrease")
    StockResponse decreaseStock(@PathVariable UUID productId,
                                @RequestBody StockUpdateRequest request);

    @PatchMapping("/internal/api/v1/products/{productId}/stock/restore")
    StockResponse restoreStock(@PathVariable UUID productId,
                               @RequestBody StockUpdateRequest request);


}
