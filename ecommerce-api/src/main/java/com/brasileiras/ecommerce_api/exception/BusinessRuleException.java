package com.brasileiras.ecommerce_api.exception;

/**
 * Exceção personalizada para regras de negócio.
 * BAD_REQUEST (400)
 */
public class BusinessRuleException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BusinessRuleException(String message) {
        super(message);
    }

}
