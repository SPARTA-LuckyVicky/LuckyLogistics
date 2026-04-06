package com.sparta.lucky.deliveryservice.infrastructure.client;

import com.sparta.lucky.deliveryservice.infrastructure.client.dto.CompanyResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="company-service")
public interface CompanyClient {

    @GetMapping
    CompanyResponse getCompanyInfo(@RequestParam UUID id);
}
