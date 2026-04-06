package com.sparta.lucky.deliveryservice.common.security.auth;

public class InternalServicePrincipal {

    private final String serviceName;

    public InternalServicePrincipal(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
