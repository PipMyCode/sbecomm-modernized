package com.sbecomm.modernized.order.domain.exception;

/**
 * Pure Java Domain Exception thrown when an order state transition is invalid.
 */
public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(String message) {
        super(message);
    }
}
