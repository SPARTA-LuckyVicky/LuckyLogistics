package com.sparta.lucky.company.presentation;

import com.sparta.lucky.company.common.exception.BusinessException;
import com.sparta.lucky.company.common.response.ApiResponse;
import com.sparta.lucky.company.application.CompanyService;
import com.sparta.lucky.company.application.dto.AssignManagerCommand;
import com.sparta.lucky.company.domain.CompanyErrorCode;
import com.sparta.lucky.company.presentation.dto.GetCompanyResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// 서비스 간 내부 호출 전용 — X-Internal-Request 헤더 필수
@RestController
@RequestMapping("/internal/api/v1/companies")
@RequiredArgsConstructor
public class CompanyInternalController {

    private final CompanyService companyService;

    private static final String INTERNAL_REQUEST_VALUE = "true";

    // order-service, product-service, user-service가 업체 검증 시 사용
    @GetMapping("/{companyId}")
    public ApiResponse<GetCompanyResDto> getCompany(
            @PathVariable UUID companyId,
            @RequestHeader("X-Internal-Request") String internalFlag
    ) {
        validateInternalRequest(internalFlag);
        return ApiResponse.success(
                GetCompanyResDto.from(companyService.getCompany(companyId))
        );
    }

    // user-service가 COMPANY_MANAGER 배정 시 호출
    @PatchMapping("/{companyId}/manager")
    public ApiResponse<Void> assignManager(
            @PathVariable UUID companyId,
            @Valid @RequestBody AssignManagerReqBody body,
            @RequestHeader("X-Internal-Request") String internalFlag
    )  {
        validateInternalRequest(internalFlag);
        companyService.assignManager(
                AssignManagerCommand.builder()
                        .companyId(companyId)
                        .managerId(body.getManagerId())
                        .build()
        );
        return ApiResponse.success();
    }

    // 내부 API 전용 Request Body (Presentation 레이어 내부 static 클래스로 관리)
    @lombok.Getter
    static class AssignManagerReqBody {
        @NotNull(message = "managerId는 필수입니다.")  // null이면 400 반환
        private UUID managerId;
    }

    // 내부 요청 검증 private 메서드
    private void validateInternalRequest(String internalFlag) {
        if (!INTERNAL_REQUEST_VALUE.equalsIgnoreCase(internalFlag)) {
            throw new BusinessException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
            // 또는 ResponseStatusException(HttpStatus.FORBIDDEN, "내부 요청만 허용됩니다.")
        }
    }
}