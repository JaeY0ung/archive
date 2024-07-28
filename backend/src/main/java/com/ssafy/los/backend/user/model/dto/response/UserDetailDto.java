package com.ssafy.los.backend.user.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * packageName    : com.ssafy.los.backend.user.model.dto.response
 * fileName       : UserDetailDto
 * author         : moongi
 * date           : 7/28/24
 * description    :
 */
@Builder
@Data
public class UserDetailDto {
    private Long id;
    private String role;
    private String email;
    private String nickname;
    private String userImg;
    private LocalDateTime birthDate;
    private Boolean gender;
    private int cash;
    private Integer singleScore;
    private Integer multiScore;
    private LocalDateTime deletedAt;
}
