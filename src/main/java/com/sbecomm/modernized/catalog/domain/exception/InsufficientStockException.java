package com.sbecomm.modernized.catalog.domain.exception;

/**
 * Pure Java Domain Exception thrown when product stock is insufficient to fulfill a request.
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
