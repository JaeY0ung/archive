package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.entity.User;

import java.util.Optional;

public interface AuthService {

    Optional<User> getLoginUser();

}
