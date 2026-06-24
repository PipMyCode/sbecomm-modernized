package com.sbecomm.modernized.order.application.exception;

/**
 * Pure Java Application Exception thrown when a requested order cannot be found.
 */
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
