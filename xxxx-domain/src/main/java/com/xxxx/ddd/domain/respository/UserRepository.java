package com.xxxx.ddd.domain.respository;

import java.util.Optional;

import com.xxxx.ddd.domain.model.entity.User;

public interface UserRepository {
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User save(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
