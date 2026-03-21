package com.psycology.backend.exception;

public class RazorpayException extends RuntimeException {

    public RazorpayException(String message) {
        super(message);
    }

    public RazorpayException(String message, Throwable cause) {
        super(message, cause);
    }
}
