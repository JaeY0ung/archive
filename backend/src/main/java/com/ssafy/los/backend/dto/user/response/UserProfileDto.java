package com.ssafy.los.backend.dto.user.response;

import com.ssafy.los.backend.util.FileUploadUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileDto {

    private Long userId;
    private String nickname;
    private String userImgName;
    private String userImg;
    private Integer singleScore;
    private Integer multiScore;

    public void loadUserImg(FileUploadUtil fileUploadUtil) {
        if (this.userImgName != null) {
            this.userImg = fileUploadUtil.getUserImg(this.userImgName);
        }
    }
}
