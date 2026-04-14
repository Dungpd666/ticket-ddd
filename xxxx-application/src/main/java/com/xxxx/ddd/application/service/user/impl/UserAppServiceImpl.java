package com.xxxx.ddd.application.service.user.impl;

import org.springframework.stereotype.Service;

import com.xxxx.ddd.application.model.UserProfileDTO;
import com.xxxx.ddd.application.service.user.UserAppService;
import com.xxxx.ddd.domain.exception.UserNotFoundException;
import com.xxxx.ddd.domain.model.entity.User;
import com.xxxx.ddd.domain.respository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAppServiceImpl implements UserAppService {

    private final UserRepository userRepository;

    @Override
    public UserProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return new UserProfileDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(), user.getCreatedAt());
    }
}
