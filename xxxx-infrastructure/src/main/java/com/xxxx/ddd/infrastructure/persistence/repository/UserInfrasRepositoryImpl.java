package com.xxxx.ddd.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.xxxx.ddd.domain.model.entity.User;
import com.xxxx.ddd.domain.respository.UserRepository;
import com.xxxx.ddd.infrastructure.persistence.mapper.UserJPAMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserInfrasRepositoryImpl implements UserRepository {

    private final UserJPAMapper userJPAMapper;

    @Override
    public Optional<User> findById(Long id) {
        return userJPAMapper.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJPAMapper.findByUsername(username);
    }

    @Override
    public User save(User user) {
        return userJPAMapper.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJPAMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJPAMapper.existsByEmail(email);
    }
}
