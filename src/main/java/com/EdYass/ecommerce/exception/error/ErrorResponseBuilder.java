package com.EdYass.ecommerce.exception.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponseBuilder {
    public static ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
        ErrorResponse errorResponse = new ErrorResponse(status.value(), status.getReasonPhrase(), message);
        return new ResponseEntity<>(errorResponse, status);
    }
}