package com.rc.mentorship.authservice.service.impl;

import com.rc.mentorship.authservice.dto.request.RegisterRequest;
import com.rc.mentorship.authservice.dto.response.UserResponse;
import com.rc.mentorship.authservice.entity.User;
import com.rc.mentorship.authservice.exception.UserNotFoundException;
import com.rc.mentorship.authservice.service.KeycloakService;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {
    @Value("${keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;

    private UsersResource usersResource;

    public UserResponse fillUserResponse(UserResponse response) {
        List<String> roles;
        try {
            roles = getUsersResource().get(getIdByEmail(response.getEmail()))
                    .roles().realmLevel().listAll()
                    .stream()
                    .map(RoleRepresentation::getName).toList();
        } catch (ForbiddenException e) {
            throw new UserNotFoundException(response.getEmail());
        }

        response.setRoles(roles);
        return response;
    }

    public void create(RegisterRequest request) {
        CredentialRepresentation credential = createPasswordCredentials(request.getPassword());
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(request.getName());
        user.setEmail(request.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        UsersResource usersResource = getUsersResource();
        usersResource.create(user);
        addRealmRoleToUser(request.getEmail(), "USER");
    }

    public void update(User user) {
        String id = user.getKeycloakId();
        UserRepresentation representation;
        try {
            List<UserRepresentation> list = getUsersResource().list();
            System.out.println(list);
            representation = getUsersResource().get(id).toRepresentation();
        } catch (Exception e) {
            throw new UserNotFoundException(user.getEmail());
        }

        representation.setEmail(user.getEmail());
        representation.setUsername(user.getName());
        getUsersResource().get(id).update(representation);
    }

    public void deleteById(String id) {
        getUsersResource().delete(id);
    }

    public String getIdByEmail(String email) {
        return getUserByEmail(email).getId();
    }

    @Override
    public UserRepresentation getUserByEmail(String email) {
        List<UserRepresentation> users = getUsersResource().searchByEmail(email, true);
        if (users.isEmpty()) {
            throw new UserNotFoundException(email);
        }
        return users.get(0);
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setTemporary(false);
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(password);
        return passwordCredential;
    }

    private void addRealmRoleToUser(String email, String roleName) {
        RealmResource realmResource = keycloak.realm(realm);
        UserRepresentation user = getUserByEmail(email);
        UserResource userResource = getUsersResource().get(user.getId());
        RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
        RoleMappingResource roleMappingResource = userResource.roles();
        roleMappingResource.realmLevel().add(Collections.singletonList(role));
    }

    private UsersResource getUsersResource() {
        if (usersResource == null) {
            usersResource = keycloak.realm(realm).users();
        }
        return usersResource;
    }
}
