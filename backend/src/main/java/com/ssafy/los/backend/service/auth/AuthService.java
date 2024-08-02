package com.ssafy.los.backend.service.auth;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.user.request.UserCreateDto;
import com.ssafy.los.backend.dto.user.response.UserDetailDto;
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
