package com.ssafy.los.backend.dto.user.response;

import com.ssafy.los.backend.util.FileUploadUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OnlineUserDto {

    private Long id;
    private String userImgName;
    private String userImg;
    private String nickname;
    private Integer singleScore;

    public void loadUserImg(FileUploadUtil fileUploadUtil) {
        if (this.userImgName != null) {
            this.userImg = fileUploadUtil.getUserImg(this.userImgName);
        }
    }


}