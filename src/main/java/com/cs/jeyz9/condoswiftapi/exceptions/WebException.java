package com.cs.jeyz9.condoswiftapi.exceptions;

import org.springframework.http.HttpStatus;

public class WebException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    
    public WebException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public WebException(String excepMessage, HttpStatus status, String message) {
        super(excepMessage);
        this.status = status;
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}
