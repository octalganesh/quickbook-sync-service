package com.octal.supa.utils;

import com.octal.supa.dto.ApiResponse;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class ExceptionResponseUtils {
    private ExceptionResponseUtils() {
        throw new IllegalStateException("this is utility class");
    }

    public static ApiResponse responseBadRequest(String errorMessage, Object data, HttpServletRequest request) {
        return new ApiResponse(errorMessage, data, String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST, new Date(), request.getContextPath());
    }

    public static ApiResponse responseInternalServerError(String errorMessage, Object data, HttpServletRequest request) {
        return new ApiResponse(errorMessage, data, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR, new Date(), request.getContextPath());
    }
}
