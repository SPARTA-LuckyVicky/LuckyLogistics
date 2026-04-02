package com.sparta.lucky.order.infrastructure.client;

import com.sparta.lucky.order.infrastructure.client.dto.ApiResponse;
import com.sparta.lucky.order.infrastructure.client.dto.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/internal/api/v1/companies/{companyId}")
    ApiResponse<CompanyResponse> getCompany(@PathVariable UUID companyId,
                                            @RequestHeader("X-Internal-Request") String internalRequest);
}
