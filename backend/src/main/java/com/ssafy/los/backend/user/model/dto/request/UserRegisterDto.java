package com.ssafy.los.backend.user.model.dto.request;

import com.ssafy.los.backend.user.model.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * packageName    : com.ssafy.los.backend.user.model.dto.request fileName       : UserRegisterDto
 * author         : moongi date           : 7/18/24 description    :
 */
@Getter
@Builder
public class UserRegisterDto {

    private String email;
    private String pwdHash;
    private LocalDateTime birthDate;
    private String nickname;
    private Boolean gender;
    private String userImg;

    public User toEntity() {
        return User.builder()
                .email(email)
                .pwdHash(pwdHash)
                .birthDate(birthDate)
                .nickname(nickname)
                .gender(gender)
                .userImg(userImg)
                .build();
    }
}
