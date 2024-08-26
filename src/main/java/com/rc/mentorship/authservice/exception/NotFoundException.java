package com.rc.mentorship.authservice.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    private static final String MESSAGE = "%s with ID: %s not found!";

    public NotFoundException(String type, UUID id) {
        super(String.format(MESSAGE, type, id));
    }
}
