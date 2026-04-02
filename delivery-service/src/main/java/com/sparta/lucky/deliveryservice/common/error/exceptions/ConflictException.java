package com.sparta.lucky.deliveryservice.common.error.exceptions;

import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConflictException extends CommonException {

    public ConflictException(ResponseCode code) {super(code);}

    public ConflictException(ResponseCode code, Exception e) {
        super(code, e);
        log.error(e.getMessage(), e);
    }
}
