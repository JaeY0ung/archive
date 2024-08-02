package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.request.UserCreateDto;
import com.ssafy.los.backend.user.model.dto.response.UserDetailDto;
import com.ssafy.los.backend.user.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    UserDetailDto getUserInfo(HttpServletRequest request);
    Long saveOAuth2User(UserCreateDto userCreateDto);
    String getAccessToken(HttpServletRequest request, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
    void requestAccess(HttpServletRequest request, HttpServletResponse response);
    User getLoginUser(); // Added method
}
