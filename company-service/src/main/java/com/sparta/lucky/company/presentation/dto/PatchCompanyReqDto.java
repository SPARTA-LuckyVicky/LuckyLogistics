package com.sparta.lucky.company.presentation.dto;

import com.sparta.lucky.company.domain.CompanyType;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

// 모든 필드 optional — null이면 해당 필드 미수정
@Getter
public class PatchCompanyReqDto {

    @Size(max = 100, message = "업체명은 100자 이하여야 합니다.")
    private String name;

    private CompanyType companyType;

    private UUID hubId;  // MASTER만 변경 가능 (Service에서 검증)

    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;
}