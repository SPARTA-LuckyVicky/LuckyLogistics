package com.sparta.lucky.deliveryservice.common.response;

import org.springframework.http.HttpStatus;

public enum ResponseCode {

    OK("DELIVERY_000", HttpStatus.OK, "OK"),
    BAD_REQUEST("DELIVERY_001", HttpStatus.BAD_REQUEST, "Bad Request"),
    VALIDATION_ERROR("DELIVERY_002", HttpStatus.BAD_REQUEST, "Validation Error"),
    NOT_READABLE("DELIVERY_003", HttpStatus.BAD_REQUEST, "Incorrect request body"),
    MISSING_HEADER("DELIVERY_004", HttpStatus.BAD_REQUEST, "Incorrect request header"),
    FORBIDDEN("DELIVERY_005", HttpStatus.FORBIDDEN, "Access Denied"),
    NOT_FOUND("DELIVERY_006", HttpStatus.NOT_FOUND, "Resource Not Found"),
    METHOD_NOT_ALLOWED("DELIVERY_007", HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed"),
    INTERNAL_ERROR("DELIVERY_008", HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

    // DELIVERY(1)
    DELIVERY_CREATED("DELIVERY_100", HttpStatus.CREATED, "Delivery Created"),
    DELIVERY_EXISTS("DELIVERY_101", HttpStatus.CONFLICT, "Delivery already exists"),
    DELIVERY_NOT_FOUND("DELIVERY_102", HttpStatus.NOT_FOUND, "Delivery Not Found"),
    DELIVERY_ALREADY_PROCESSED("DELIVERY_103", HttpStatus.CONFLICT, "Delivery Already Processed"),

    // DELIVERY ROUTE (2)
    ROUTE_RESPONSE_NULL("DELIVERY_203", HttpStatus.INTERNAL_SERVER_ERROR, "Route Response is Null"),

    // DRIVER(3),
    DRIVER_CREATED("DELIVERY_300", HttpStatus.CREATED, "Driver Created"),
    DRIVER_EXISTS("DELIVERY_301", HttpStatus.CONFLICT, "Driver already exists"),
    DRIVER_NOT_FOUND("DELIVERY_302", HttpStatus.NOT_FOUND, "Driver not found"),
    NO_DRIVER_AVAILABLE("DELIVERY_303", HttpStatus.NOT_FOUND, "No Drivers Available");

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
