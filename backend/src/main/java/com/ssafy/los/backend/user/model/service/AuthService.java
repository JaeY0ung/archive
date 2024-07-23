package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.request.UserCreateRequest;
import com.ssafy.los.backend.user.model.entity.User;

public interface AuthService {

    User getLoginUser();

    void registerUser(UserCreateRequest request);
}
