package com.sparta.lucky.company.presentation.dto;

import com.sparta.lucky.company.domain.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PostCompanyReqDto {

    @NotBlank(message = "업체명은 필수 항목입니다.")
    @Size(max = 100, message = "업체명은 100자 이하여야 합니다.")
    private String name;

    @NotNull(message = "업체 유형은 필수 항목입니다.")
    private CompanyType companyType;

    @NotNull(message = "소속 허브 ID는 필수 항목입니다.")
    private UUID hubId;

    @NotBlank(message = "주소는 필수 항목입니다.")
    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;
}