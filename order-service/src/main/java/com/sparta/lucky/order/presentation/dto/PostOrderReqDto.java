package com.sparta.lucky.order.presentation.dto;

import com.sparta.lucky.order.application.dto.CreateOrderCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class PostOrderReqDto {

    @NotNull(message = "요청 업체 ID는 필수입니다.")
    private UUID requesterCompanyId;

    @NotNull(message = "수령 업체 ID는 필수입니다.")
    private UUID receiverCompanyId;

    @NotNull(message = "상품 ID는 필수입니다.")
    private UUID productId;

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private Integer quantity;

    private String requestNote;

    private LocalDateTime requestedDeadline;

    public CreateOrderCommand toCommand() {
        return new CreateOrderCommand(
                requesterCompanyId,
                receiverCompanyId,
                productId,
                quantity,
                requestNote,
                requestedDeadline
        );
    }
}
