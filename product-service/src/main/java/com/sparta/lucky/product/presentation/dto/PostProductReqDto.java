package com.sparta.lucky.product.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PostProductReqDto {

    @NotNull(message = "소속 업체 UUID는 필수 항목입니다.")
    private UUID companyId;

    @NotNull(message = "소속 허브 UUID는 필수 항목입니다.")
    private UUID hubId;

    @NotBlank(message = "상품명은 필수 항목입니다.")
    @Size(max = 100, message = "상품명은 100자 이하여야 합니다.")
    private String name;

    @NotNull(message = "상품 단가는 필수 항목입니다.")
    @Min(value = 0, message = "상품 단가는 0 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "재고는 필수 항목입니다.")
    @Min(value = 0, message = "초기 재고는 0 이상이어야 합니다.")
    private Integer stock;
}
