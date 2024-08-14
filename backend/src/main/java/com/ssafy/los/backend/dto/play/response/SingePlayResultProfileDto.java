package com.ssafy.los.backend.dto.play.response;

import com.ssafy.los.backend.util.FileUploadUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SingePlayResultProfileDto {

    private String nickname;

    private String userImgName;

    private String userImg;

    private Long sheetId;

    private String sheetTitle;

    private String songComposer;

    private String songImgName;

    private String songImg;

    private String uploaderNickname;

    private Integer level;

    private float score;

//    private Integer point;

    private Long playTime;

    public void loadUserImg(FileUploadUtil fileUploadUtil) {
        if (this.userImgName != null) {
            this.userImg = fileUploadUtil.getUserImg(this.userImgName);
        }
    }

    public void loadSongImg(FileUploadUtil fileUploadUtil) {
        if (this.songImgName != null) {
            this.songImg = fileUploadUtil.getSongImgByFileName(this.songImgName);
        }
    }
}
