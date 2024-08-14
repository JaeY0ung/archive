package com.ssafy.los.backend.dto.user.response;

import com.ssafy.los.backend.util.FileUploadUtil;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * packageName    : com.ssafy.los.backend.dto.user.response fileName       : UserDetailDto author :
 * moongi date           : 7/28/24 description    :
 */
@Builder
@Data
public class UserDetailDto {

    private Long id;
    private String role;
    private String email;
    private String nickname;
    private String userImgName;
    private String userImg;
    private LocalDateTime birthDate;
    private Boolean gender;
    private Integer cash;
    private Integer singleScore;
    private Integer multiScore;
    private LocalDateTime deletedAt;

    public void loadUserImg(FileUploadUtil fileUploadUtil) {
        if (this.userImgName != null) {
            this.userImg = fileUploadUtil.getUserImg(this.userImgName);
        }
    }
}
