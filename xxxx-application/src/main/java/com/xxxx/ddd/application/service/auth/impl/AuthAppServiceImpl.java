package com.xxxx.ddd.application.service.auth.impl;

import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.xxxx.ddd.application.model.AuthResponse;
import com.xxxx.ddd.application.service.auth.AuthAppService;
import com.xxxx.ddd.domain.exception.UnauthorizedException;
import com.xxxx.ddd.domain.model.entity.User;
import com.xxxx.ddd.domain.model.enums.UserRole;
import com.xxxx.ddd.domain.respository.UserRepository;
import com.xxxx.ddd.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthAppServiceImpl implements AuthAppService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse register(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        User user = new User()
                .setUsername(username)
                .setEmail(email)
                .setPasswordHash(passwordEncoder.encode(password))
                .setRole(UserRole.USER)
                .setCreatedAt(new Date())
                .setUpdatedAt(new Date());

        User saved = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(saved.getId(), saved.getRole().name());
        return new AuthResponse(token, saved.getId(), saved.getRole().name());
    }

    @Override
    public AuthResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getRole().name());
    }
}
