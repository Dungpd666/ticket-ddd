package com.xxxx.ddd.application.service.auth;

import com.xxxx.ddd.application.model.AuthResponse;

public interface AuthAppService {
    AuthResponse register(String username, String email, String password);

    AuthResponse login(String username, String password);
}
