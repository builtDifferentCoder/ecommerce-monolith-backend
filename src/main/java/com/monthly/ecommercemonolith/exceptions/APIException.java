package com.monthly.ecommercemonolith.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class APIException extends RuntimeException{
    private static final Long serialVersionId=1l;

    public APIException() {
    }
    public APIException(String message) {
        super(message);
    }
}
