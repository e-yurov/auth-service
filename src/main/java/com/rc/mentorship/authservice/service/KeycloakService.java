package com.rc.mentorship.authservice.service;

import com.rc.mentorship.authservice.dto.request.RegisterRequest;
import com.rc.mentorship.authservice.dto.response.UserResponse;
import com.rc.mentorship.authservice.entity.User;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakService {
    UserResponse fillUserResponse(UserResponse response);

    void addUser(RegisterRequest request);

    void updateUser(User user);

    void deleteUserById(String id);

    String getKeycloakIdByEmail(String email);

    UserRepresentation getKeycloakUserByEmail(String email);
}
