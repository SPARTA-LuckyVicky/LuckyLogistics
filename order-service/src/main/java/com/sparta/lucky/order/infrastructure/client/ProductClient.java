package com.sparta.lucky.order.infrastructure.client;

import com.sparta.lucky.order.infrastructure.client.dto.FeignApiResponse;
import com.sparta.lucky.order.infrastructure.client.dto.ProductResponse;
import com.sparta.lucky.order.infrastructure.client.dto.StockResponse;
import com.sparta.lucky.order.infrastructure.client.dto.StockUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name="product-service")
public interface ProductClient {

    @GetMapping("/internal/api/v1/products/{productId}")
    FeignApiResponse<ProductResponse> getProduct(
                @PathVariable UUID productId,
                @RequestHeader("X-Internal-Request") String internalRequest);

    @PatchMapping("/internal/api/v1/products/{productId}/stock/decrease")
    FeignApiResponse<StockResponse> decreaseStock(@PathVariable UUID productId,
                                @RequestBody StockUpdateRequest request,
                                @RequestHeader("X-Internal-Request") String internalRequest);

    @PatchMapping("/internal/api/v1/products/{productId}/stock/restore")
    FeignApiResponse<StockResponse> restoreStock(@PathVariable UUID productId,
                               @RequestBody StockUpdateRequest request,
                               @RequestHeader("X-Internal-Request") String internalRequest);


}
