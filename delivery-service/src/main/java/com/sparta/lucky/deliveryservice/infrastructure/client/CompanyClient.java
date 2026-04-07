package com.sparta.lucky.deliveryservice.infrastructure.client;

import com.sparta.lucky.deliveryservice.infrastructure.client.dto.CompanyResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="company-service")
public interface CompanyClient {

    @GetMapping("/internal/api/v1/companies/{companyId}")
    CompanyResponse getCompanyInfo(@PathVariable("companyId") UUID companyId);
}
