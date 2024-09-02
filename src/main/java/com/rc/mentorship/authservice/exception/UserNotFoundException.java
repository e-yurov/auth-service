package com.rc.mentorship.authservice.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    private static final String MESSAGE = "User%s not found!";

    public UserNotFoundException() {
        super(String.format(MESSAGE, ""));
    }

    public UserNotFoundException(String email) {
        super(String.format(MESSAGE, " with email: " + email));
    }

    public UserNotFoundException(UUID keycloakId) {
        super(String.format(MESSAGE, " with keycloak id: " + keycloakId));
    }
}
