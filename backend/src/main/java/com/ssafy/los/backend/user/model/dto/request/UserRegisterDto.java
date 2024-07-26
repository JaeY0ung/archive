package com.ssafy.los.backend.user.model.dto.request;

import com.ssafy.los.backend.user.model.entity.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * packageName    : com.ssafy.los.backend.user.model.dto.request fileName       : UserRegisterDto
 * author         : moongi date           : 7/18/24 description    :
 */
@Getter
@ToString
public class UserRegisterDto {

    private String email;
    private String password; // 여기는 Hash 값이 들어가지 않음
    private LocalDateTime birthDate;
    private String nickname;
    private Boolean gender;

    @Builder
    public User toEntity(String hashPwd, String role) {
        return User.builder()
                .email(email)
                .pwdHash(hashPwd) // 암호화된 패스워드 추가
                .birthDate(birthDate)
                .nickname(nickname)
                .gender(gender)
                .role(role) // 롤 추가
                .build();
    }
}
