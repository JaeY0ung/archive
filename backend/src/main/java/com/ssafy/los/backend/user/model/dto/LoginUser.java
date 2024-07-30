package com.ssafy.los.backend.user.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginUser {
    private String id;
    private String email;

    // 기본 생성자
    public LoginUser() {
    }

    // 빌더 패턴
    @Builder
    public LoginUser(String id, String email) {
        this.id = id;
        this.email = email;
    }
}