package com.ssafy.los.backend.dto;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.util.FileUploadUtil;
import lombok.Builder;
import lombok.Getter;

/**
 * packageName    : com.ssafy.los.backend.like.model.dto fileName       : FollowSimpleListDto author
 * : moongi date           : 7/22/24 description    :
 */
@Getter
public class FollowSimpleListDto {

    private String nickname;
    private String userImgName;
    private String userImg;

    @Builder
    public FollowSimpleListDto(String nickname, String userImgName) {
        this.nickname = nickname;
        this.userImgName = userImgName;
    }

    public void loadUserImg(FileUploadUtil fileUploadUtil) {
        if (this.userImgName != null) {
            this.userImg = fileUploadUtil.getUserImg(this.userImgName);
        }
    }

}
