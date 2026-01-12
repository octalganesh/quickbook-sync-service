package com.octal.fsm.model.enums;

public enum HttpRequestTypes {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    ;

    private final String status;

    HttpRequestTypes(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
