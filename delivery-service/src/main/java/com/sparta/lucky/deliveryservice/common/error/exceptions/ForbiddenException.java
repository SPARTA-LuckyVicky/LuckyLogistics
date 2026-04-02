package com.sparta.lucky.deliveryservice.common.error.exceptions;

import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ForbiddenException extends CommonException {

    public ForbiddenException(ResponseCode code) {super(code);}

    public ForbiddenException(ResponseCode code, Exception e) {
        super(code, e);
        log.error(e.getMessage(), e);
    }
}
