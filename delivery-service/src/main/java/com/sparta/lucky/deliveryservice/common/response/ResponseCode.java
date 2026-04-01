package com.sparta.lucky.deliveryservice.common.response;

import org.springframework.http.HttpStatus;

public enum ResponseCode {

    OK("DELIVERY_200", HttpStatus.OK, "OK"),
    BAD_REQUEST("DELIVERY_400", HttpStatus.BAD_REQUEST, "Bad Request"),
    VALIDATION_ERROR("DELIVERY_4001", HttpStatus.BAD_REQUEST, "Validation Error"),
    NOT_READABLE("DELIVERY_4002", HttpStatus.BAD_REQUEST, "Incorrect request body"),
    MISSING_HEADER("DELIVERY_4003", HttpStatus.BAD_REQUEST, "Incorrect request header"),
    FORBIDDEN("DELIVERY_403", HttpStatus.FORBIDDEN, "Access Denied"),
    NOT_FOUND("DELIVERY_404", HttpStatus.NOT_FOUND, "Resource Not Found"),
    METHOD_NOT_ALLOWED("DELIVERY_405", HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed"),
    INTERNAL_ERROR("DELIVERY_500", HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

    DRIVER_EXISTS("DELIVERY_4093", HttpStatus.CONFLICT, "Driver already exists"),
    ;

    private final String code;
    private final HttpStatus status;
    private final String message;

    ResponseCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }
}
