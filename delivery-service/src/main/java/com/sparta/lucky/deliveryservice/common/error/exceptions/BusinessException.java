package com.sparta.lucky.deliveryservice.common.error.exceptions;

import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessException extends CommonException{

    public BusinessException(ResponseCode code) {super(code);}

    public BusinessException(ResponseCode code, Exception e) {
        super(code, e);
        log.error(e.getMessage(), e);
    }
}
