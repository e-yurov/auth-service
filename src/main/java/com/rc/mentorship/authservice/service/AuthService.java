package com.rc.mentorship.authservice.service;

import com.rc.mentorship.authservice.dto.request.LoginRequest;
import com.rc.mentorship.authservice.dto.request.RegisterRequest;
import com.rc.mentorship.authservice.dto.response.JwtResponse;
import com.rc.mentorship.authservice.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest registerRequest);

    JwtResponse login(LoginRequest loginRequest);
}
