package com.sparta.lucky.company.infrastructure.feign;

import com.sparta.lucky.company.infrastructure.feign.dto.BulkDeleteResponse;
import com.sparta.lucky.company.infrastructure.feign.dto.FeignApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

// product-service 내부 API - 업체 삭제 시 하위 상품 일괄 soft delete
@FeignClient(name = "product-service")
public interface ProductInternalClient {

    @DeleteMapping("/internal/api/v1/products/company/{companyId}")
    FeignApiResponse<BulkDeleteResponse> deleteProductsByCompany(
            @PathVariable("companyId") UUID companyId,
            @RequestHeader("X-Internal-Request") String internalRequest,
            @RequestHeader("X-User-Id") UUID deletedBy // Audit 주체
    );
}