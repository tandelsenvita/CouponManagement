package com.coupon.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public class ErrorResponsePayload {
    private Date timestamp;
    private Integer code;
    private HttpStatus status;
    private String message;
    private String description;


    public ErrorResponsePayload(Date timestamp, int code, HttpStatus status, String message, String description) {
        this.timestamp = timestamp;
        this.code = code;
        this.status = status;
        this.message = message;
        this.description = description;
    }

    public ResponseEntity<Object> buildResponse() {
        return new ResponseEntity(this, this.status);
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public Integer getCode() {
        return this.code;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }


    public String getDescription() {
        return this.description;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setCode(final Integer code) {
        this.code = code;
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public void setMessage(final String message) {
        this.message = message;
    }


    public void setDescription(final String description) {
        this.description = description;
    }

    public ErrorResponsePayload(final Date timestamp, final Integer code, final HttpStatus status, final String message,  final String description) {
        this.timestamp = timestamp;
        this.code = code;
        this.status = status;
        this.message = message;
        this.description = description;
    }

    public ErrorResponsePayload() {
    }
}

