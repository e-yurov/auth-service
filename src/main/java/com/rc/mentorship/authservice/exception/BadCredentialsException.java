package com.rc.mentorship.authservice.exception;

public class BadCredentialsException extends RuntimeException {
    private static final String MESSAGE = "Wrong password!";

    public BadCredentialsException() {
        super(MESSAGE);
    }
}
