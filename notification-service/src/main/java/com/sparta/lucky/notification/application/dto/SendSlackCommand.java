package com.sparta.lucky.notification.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendSlackCommand {

    private String receiverSlackId;
    private String messageContent;
    private UUID relatedOrderId;
    private UUID senderId;

}
