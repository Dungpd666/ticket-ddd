package com.xxxx.ddd.application.service.user;

import com.xxxx.ddd.application.model.UserProfileDTO;

public interface UserAppService {
    UserProfileDTO getProfile(Long userId);
}
