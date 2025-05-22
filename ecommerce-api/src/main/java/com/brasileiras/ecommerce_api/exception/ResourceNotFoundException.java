package com.brasileiras.ecommerce_api.exception;
/**
 * Exceção personalizada para regras de negócio violadas.
 * NOT_FOUND (404)
 */

public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
