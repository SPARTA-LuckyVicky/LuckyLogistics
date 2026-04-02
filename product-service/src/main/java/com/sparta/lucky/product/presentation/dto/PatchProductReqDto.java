package com.sparta.lucky.product.presentation.dto;

import com.sparta.lucky.product.domain.ProductStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

// 모든 필드 optional — null이면 해당 필드 미수정
@Getter
public class PatchProductReqDto {

    @Size(max = 100, message = "상품명은 100자 이하여야 합니다.")
    @Pattern(regexp = ".*\\S.*", message = "상품명은 공백만 입력할 수 없습니다.")
    private String name;

    private Integer price;

    private ProductStatus status;

    private UUID companyId;

    private UUID hubId;
}
