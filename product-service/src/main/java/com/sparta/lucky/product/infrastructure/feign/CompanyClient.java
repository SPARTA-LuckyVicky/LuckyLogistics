package com.sparta.lucky.product.infrastructure.feign;

import com.sparta.lucky.product.infrastructure.feign.dto.CompanyResponse;
import com.sparta.lucky.product.infrastructure.feign.dto.FeignApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/internal/api/v1/companies/{companyId}")
    FeignApiResponse<CompanyResponse> getCompany(
            @PathVariable UUID companyId,
            @RequestHeader("X-Internal-Request") String internalRequest
    );
}