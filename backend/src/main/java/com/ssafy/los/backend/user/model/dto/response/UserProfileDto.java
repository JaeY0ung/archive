package com.ssafy.los.backend.user.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileDto {

    private Long userId;
    private String nickname;
    private String userImg;
    private int singleScore;

}
