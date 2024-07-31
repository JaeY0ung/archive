package com.ssafy.los.backend.user.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponseDto {

    private Long userId;
    private String nickname;
    private String userImg;
    private int singleScore;

}
