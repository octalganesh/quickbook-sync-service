package com.octal.supa.exceptions;

import com.octal.supa.dto.ApiResponse;
import com.octal.supa.utils.ExceptionResponseUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGS = LogManager.getLogger(CommonExceptionHandler.class);


    @ExceptionHandler({ActorPayException.class})
    public ResponseEntity<ApiResponse> handleActorPayException(ActorPayException ex, HttpServletRequest request, HttpServletResponse response) {
        LOGS.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ExceptionResponseUtils.responseBadRequest(ex.getMessage(), null, request), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<ApiResponse> handleNullPointerException(ActorPayException ex, HttpServletRequest request, HttpServletResponse response) {
        LOGS.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ExceptionResponseUtils.responseInternalServerError(ex.getMessage(), null, request), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public final ResponseEntity<ApiResponse> handleObjectNotFoundException(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(ExceptionResponseUtils.responseBadRequest(ex.getMessage(), null, request), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        // Get all errors and prepare a list of errors
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(
                        fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("message", errors.get(0));
        body.put("data", null);
        LOGS.error(errors);
        return new ResponseEntity<>(body, headers, status);

    }
}
