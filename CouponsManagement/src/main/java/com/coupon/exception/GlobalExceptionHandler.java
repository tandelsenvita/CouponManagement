package com.coupon.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> globalExceptionHandler(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex.fillInStackTrace());
        return (new ErrorResponsePayload(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false))).buildResponse();
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleJsonParseException(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("Invalid JSON format: {}", ex.getMessage(), ex.fillInStackTrace());
        return (new ErrorResponsePayload(new Date(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "Invalid JSON format: " + ex.getLocalizedMessage(), request.getDescription(false))).buildResponse();
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> illegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error(ex.getMessage(), ex.fillInStackTrace());
        return (new ErrorResponsePayload(new Date(), HttpStatus.NOT_ACCEPTABLE.value(), HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), request.getDescription(false))).buildResponse();
    }
}

