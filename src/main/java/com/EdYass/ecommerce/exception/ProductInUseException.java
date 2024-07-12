package com.EdYass.ecommerce.exception;

public class ProductInUseException extends RuntimeException {
    public ProductInUseException(String message) {
        super(message);
    }
}
