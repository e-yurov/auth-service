package com.rc.mentorship.authservice.util;

import com.rc.mentorship.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class UserIdPermissionEvaluator implements PermissionEvaluator {
    private final UserService userService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        Jwt credentials = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return userService.getUserIdByKeycloakId(credentials.getSubject()).equals(targetDomainObject);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
