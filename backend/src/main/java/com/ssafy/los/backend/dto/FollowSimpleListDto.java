package com.ssafy.los.backend.dto;

import com.ssafy.los.backend.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

/**
 * packageName    : com.ssafy.los.backend.like.model.dto fileName       : FollowSimpleListDto author
 * : moongi date           : 7/22/24 description    :
 */
@Getter
public class FollowSimpleListDto {

    private String nickname;
    private String userImg;

    @Builder
    public FollowSimpleListDto(String nickname, String userImg) {
        this.nickname = nickname;
        this.userImg = userImg;
    }

    public User toEntity() {
        return User.builder()
                .nickname(nickname)
                .userImg(userImg)
                .build();
    }
}
