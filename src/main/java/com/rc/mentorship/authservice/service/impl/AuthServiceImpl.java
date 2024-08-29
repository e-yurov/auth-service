package com.rc.mentorship.authservice.service.impl;

import com.rc.mentorship.authservice.dto.request.LoginRequest;
import com.rc.mentorship.authservice.dto.request.RegisterRequest;
import com.rc.mentorship.authservice.dto.response.JwtResponse;
import com.rc.mentorship.authservice.dto.response.UserResponse;
import com.rc.mentorship.authservice.entity.User;
import com.rc.mentorship.authservice.exception.BadCredentialsException;
import com.rc.mentorship.authservice.exception.UserAlreadyExistsException;
import com.rc.mentorship.authservice.exception.UserNotFoundException;
import com.rc.mentorship.authservice.mapper.UserMapper;
import com.rc.mentorship.authservice.properties.AuthKeycloakProperties;
import com.rc.mentorship.authservice.repository.UserRepository;
import com.rc.mentorship.authservice.service.AuthService;
import com.rc.mentorship.authservice.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakService keycloakService;
    private final AuthKeycloakProperties properties;

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

    @Override
    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest loginRequest) {
        if (!userRepository.existsByEmail(loginRequest.getEmail())) {
            throw new UserNotFoundException(loginRequest.getEmail());
        }

        String token = getAccessToken(loginRequest);
        return new JwtResponse(token);
    }

    private String getAccessToken(LoginRequest request) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", request.getEmail());
        body.add("password", request.getPassword());
        body.add("grant_type", "password");
        body.add("client_id", properties.getClientId());
        body.add("client_secret", properties.getClientSecret());

        WebClient client = WebClient.builder().build();
        Map<String, String> result = client.post()
                .uri(properties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(body))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>(){})
                .doOnError((e) -> {
                    throw new BadCredentialsException();
                })
                .block();

        if (result == null || result.get("access_token") == null) {
            throw new BadCredentialsException();
        }
        return result.get("access_token");
    }
}
