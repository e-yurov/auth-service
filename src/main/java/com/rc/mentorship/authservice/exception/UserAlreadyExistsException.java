package com.rc.mentorship.authservice.exception;

public class UserAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE = "User with email: %s already registered!";

    public UserAlreadyExistsException(String email) {
        super(String.format(MESSAGE, email));
    }
}
