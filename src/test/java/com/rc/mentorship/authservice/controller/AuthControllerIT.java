package com.rc.mentorship.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rc.mentorship.authservice.container.KeycloakPostgresContainerIT;
import com.rc.mentorship.authservice.dto.request.RegisterRequest;
import com.rc.mentorship.authservice.dto.response.UserResponse;
import com.rc.mentorship.authservice.entity.User;
import com.rc.mentorship.authservice.mapper.UserMapper;
import com.rc.mentorship.authservice.repository.UserRepository;
import com.rc.mentorship.authservice.service.KeycloakService;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.AbstractUserRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIT extends KeycloakPostgresContainerIT {
    private static final String URL = "/api/v1/auth";

    private static final String NAME = "user";
    private static final String EMAIL = "user@test.com";
    private static final String PASSWORD = "Password";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakService keycloakService;

    @Autowired
    public AuthControllerIT(MockMvc mockMvc,
                            ObjectMapper objectMapper,
                            UserRepository userRepository,
                            UserMapper userMapper,
                            KeycloakService keycloakService) {
        super(mockMvc, objectMapper);
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.keycloakService = keycloakService;
    }

    @Test
    void register_SimpleValues_ReturningToken() throws Exception {
        RegisterRequest request = new RegisterRequest(NAME, EMAIL, PASSWORD);

        MvcResult mvcResult = mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andReturn();
        UserResponse result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserResponse.class);
        Optional<User> actualInDb = userRepository.findByEmail(EMAIL);
        UserRepresentation keycloakUser = keycloakService.getKeycloakUserByEmail(EMAIL);

        assertThat(result).isNotNull()
                .extracting(UserResponse::getName, UserResponse::getEmail)
                .containsExactly(NAME, EMAIL);
        assertThat(actualInDb).isPresent();
        assertThat(userMapper.toDto(actualInDb.get())).isEqualTo(result);
        assertThat(keycloakUser).isNotNull()
                .extracting(AbstractUserRepresentation::getUsername, AbstractUserRepresentation::getEmail)
                .containsExactly(NAME, EMAIL);
    }

    @Test
    @Sql("/sql/insert_user.sql")
    void register_HasUserWithSuchEmail_ReturningBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest(NAME, EMAIL, PASSWORD);

        mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }
}
