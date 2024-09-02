package com.rc.mentorship.authservice.service;

import com.rc.mentorship.authservice.dto.request.UserUpdateRequest;
import com.rc.mentorship.authservice.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface UserService {
    Page<UserResponse> findAll(PageRequest pageRequest);

    UserResponse findById(UUID id);

    UserResponse update(UserUpdateRequest toUpdate);

    void delete(UUID id);

    String getUserIdByKeycloakIdInAuthentication();
}
