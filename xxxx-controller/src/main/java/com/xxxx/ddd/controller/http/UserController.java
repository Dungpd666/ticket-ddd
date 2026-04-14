package com.xxxx.ddd.controller.http;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xxxx.ddd.application.model.UserProfileDTO;
import com.xxxx.ddd.application.service.user.UserAppService;
import com.xxxx.ddd.controller.model.enums.ResultUtil;
import com.xxxx.ddd.controller.model.vo.ResultMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserAppService userAppService;

    @GetMapping("/me")
    public ResultMessage<UserProfileDTO> me() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResultUtil.data(userAppService.getProfile(userId));
    }
}
