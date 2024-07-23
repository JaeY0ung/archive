package com.ssafy.los.backend.user.model.dto.response;

import com.ssafy.los.backend.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;

/**
 * packageName    : com.ssafy.los.backend.user.model.dto.request fileName       : UserMyPageDto
 * author         : moongi date           : 7/18/24 description    :
 */
@Getter
@Builder
public class UserMyPageDto {

    private String email;

    public User toEntity() {
        return User.builder()
                .email(email)
                .build();
    }
}
