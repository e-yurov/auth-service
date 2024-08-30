package com.rc.mentorship.authservice.service.impl;

import com.rc.mentorship.authservice.dto.request.UserUpdateRequest;
import com.rc.mentorship.authservice.dto.response.UserResponse;
import com.rc.mentorship.authservice.entity.User;
import com.rc.mentorship.authservice.exception.NotFoundException;
import com.rc.mentorship.authservice.mapper.UserMapper;
import com.rc.mentorship.authservice.repository.UserRepository;
import com.rc.mentorship.authservice.service.KeycloakService;
import com.rc.mentorship.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeycloakService keycloakService;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest)
                .map(userMapper::toDto).map(keycloakService::fillUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        UserResponse response = userMapper.toDto(userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User", id)
        ));
        return keycloakService.fillUserResponse(response);
    }

    @Override
    @Transactional
    @CachePut(key = "#toUpdate.id", value = "users")
    public UserResponse update(UserUpdateRequest toUpdate) {
        Optional<User> inDb = userRepository.findById(toUpdate.getId());
        if (inDb.isEmpty()) {
            throw new NotFoundException("User", toUpdate.getId());
        }
        User user = userMapper.toEntity(toUpdate);
        user.setKeycloakId(inDb.get().getKeycloakId());
        userRepository.save(user);
        keycloakService.update(user);
        return keycloakService.fillUserResponse(userMapper.toDto(user));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id", value = "users")
    public void delete(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return;
        }
        User user = userOptional.get();
        userRepository.delete(user);
        keycloakService.deleteById(user.getKeycloakId());
    }
}
