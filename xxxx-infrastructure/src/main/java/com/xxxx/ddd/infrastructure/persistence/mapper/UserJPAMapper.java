package com.xxxx.ddd.infrastructure.persistence.mapper;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xxxx.ddd.domain.model.entity.User;

public interface UserJPAMapper extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
