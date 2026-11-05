package com.AgricultureFMSystem.AgricultureFMSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ============================================================================================
 * Phase 2: Custom Exception Handling - DuplicateResourceException
 * ============================================================================================
 * Thrown when attempting to create a record that conflicts with an existing unique constraint
 * (e.g. duplicate email address).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
