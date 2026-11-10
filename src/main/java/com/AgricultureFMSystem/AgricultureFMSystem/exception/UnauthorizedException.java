package com.AgricultureFMSystem.AgricultureFMSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ============================================================================================
 * Phase 2 & 5: Custom UnauthorizedException (HTTP 401)
 * ============================================================================================
 * Purpose: Thrown when credentials or token authentication fail
 * ============================================================================================
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}