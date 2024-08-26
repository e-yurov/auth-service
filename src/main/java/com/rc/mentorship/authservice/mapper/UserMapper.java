package com.rc.mentorship.authservice.mapper;

import com.rc.mentorship.authservice.dto.request.RegisterRequest;
import com.rc.mentorship.authservice.dto.request.UserUpdateRequest;
import com.rc.mentorship.authservice.dto.response.UserResponse;
import com.rc.mentorship.authservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserUpdateRequest requestDto);

    User toEntity(RegisterRequest registerRequest);

    UserResponse toDto(User entity);
}
