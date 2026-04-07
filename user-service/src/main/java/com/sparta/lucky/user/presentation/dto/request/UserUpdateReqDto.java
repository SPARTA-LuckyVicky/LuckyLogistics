package com.sparta.lucky.user.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateReqDto {
    private String name;
    private String receiverSlackId;
    private String hubId;
    private String companyId;
}