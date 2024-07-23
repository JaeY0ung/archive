package com.ssafy.los.backend.auth_mg.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.ssafy.los.backend.auth_mg.model.dto
 * fileName       : TokenDto
 * author         : moongi
 * date           : 7/23/24
 * description    :
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {

    // grantType 필드는 JWT에 대한 인증 타입입니다. 이번 포스팅에선 Bearer 인증 방식을 사용
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
}
