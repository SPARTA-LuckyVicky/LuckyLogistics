package com.sparta.lucky.user.presentation.dto.request;

import com.sparta.lucky.user.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdateRequest {
    private UserStatus status; // PENDING, APPROVE, REJECTED 중 하나
}
