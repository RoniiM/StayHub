package com.stayhub.exception;

public class InvalidBookingStatusTransitionException extends RuntimeException {

    public InvalidBookingStatusTransitionException(String message) {
        super(message);
    }
}
