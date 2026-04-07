package com.sparta.lucky.user.infrastructure.client;

import com.sparta.lucky.user.common.response.ApiResponse;
import com.sparta.lucky.user.infrastructure.client.dto.AssignManagerReqBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @PatchMapping("/internal/api/v1/companies/{companyId}/manager")
    ApiResponse<Void> assignManager(
            @PathVariable("companyId") UUID companyId,
            @RequestBody AssignManagerReqBody body,
            @RequestHeader("X-Internal-Request") String internalFlag
    );
}
