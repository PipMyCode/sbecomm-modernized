package com.sbecomm.modernized.cart.application.exception;

/**
 * Pure Java Application Exception thrown when attempting to process an empty cart.
 */
public class CartEmptyException extends RuntimeException {
    public CartEmptyException(String message) {
        super(message);
    }
}
