package com.rc.mentorship.authservice.service;

import com.rc.mentorship.authservice.dto.request.RegisterRequest;
import com.rc.mentorship.authservice.dto.response.UserResponse;
import com.rc.mentorship.authservice.entity.User;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakService {
    UserResponse fillUserResponse(UserResponse response);

    void create(RegisterRequest request);

    void update(User user);

    void deleteById(String id);

    String getIdByEmail(String email);

    UserRepresentation getUserByEmail(String email);
}
