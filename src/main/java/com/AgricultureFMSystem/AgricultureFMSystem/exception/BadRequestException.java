package com.AgricultureFMSystem.AgricultureFMSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ============================================================================================
 * Phase 2: Custom Exception Handling - BadRequestException
 * ============================================================================================
 * Thrown for illegal arguments, invalid state transitions, or business rule violations.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
