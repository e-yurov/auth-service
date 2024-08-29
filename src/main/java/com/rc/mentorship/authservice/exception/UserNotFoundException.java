package com.rc.mentorship.authservice.exception;

public class UserNotFoundException extends RuntimeException {
    private static final String MESSAGE = "User with email: %s not found!";

    public UserNotFoundException(String email) {
        super(String.format(MESSAGE, email));
    }
}
