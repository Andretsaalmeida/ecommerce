package com.brasileiras.ecommerce_api.exception;
/**
 * CONFLICT (409)
 */

public class DataConflictException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataConflictException(String message) {
        super(message);
    }

}
