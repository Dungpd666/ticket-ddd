package com.xxxx.ddd.controller.http;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xxxx.ddd.application.model.AuthResponse;
import com.xxxx.ddd.application.service.auth.AuthAppService;
import com.xxxx.ddd.controller.model.enums.ResultUtil;
import com.xxxx.ddd.controller.model.request.AuthRequest;
import com.xxxx.ddd.controller.model.request.RegisterRequest;
import com.xxxx.ddd.controller.model.vo.ResultMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthAppService authAppService;

    @PostMapping("/register")
    public ResultMessage<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResultUtil.data(
                authAppService.register(request.getUsername(), request.getEmail(), request.getPassword()));
    }

    @PostMapping("/login")
    public ResultMessage<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        return ResultUtil.data(
                authAppService.login(request.getUsername(), request.getPassword()));
    }
}
