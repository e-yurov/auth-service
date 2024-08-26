package com.rc.mentorship.authservice.service.impl;

import com.rc.mentorship.authservice.dto.request.RegisterRequest;
import com.rc.mentorship.authservice.dto.response.UserResponse;
import com.rc.mentorship.authservice.entity.User;
import com.rc.mentorship.authservice.exception.UserAlreadyExistsException;
import com.rc.mentorship.authservice.mapper.UserMapper;
import com.rc.mentorship.authservice.repository.UserRepository;
import com.rc.mentorship.authservice.service.AuthService;
import com.rc.mentorship.authservice.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakService keycloakService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }
        keycloakService.addUser(registerRequest);
        String keycloakId = keycloakService.getKeycloakIdByEmail(email);
        User user = userMapper.toEntity(registerRequest);
        user.setKeycloakId(keycloakId);
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
