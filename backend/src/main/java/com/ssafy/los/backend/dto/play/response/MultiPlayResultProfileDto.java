package com.ssafy.los.backend.dto.play.response;

import com.ssafy.los.backend.util.FileUploadUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiPlayResultProfileDto {
    private String myNickname;

    private String myProfileImgName;

    private String myProfileImg;

    private Float myScore;

    private String otherNickname;

    private String otherProfileImg;

    private String otherProfileImgName;

    private Float otherScore;

    private String sheetTitle;

    private String songComposer;

    private String songImgName;

    private String songImg;

    private String uploaderNickname;

    private Integer level;

    private long playTime; // 플레이 시각을 초 단위로 저장

    private Boolean draw;

    public void loadUserImg(FileUploadUtil fileUploadUtil) {
        if (this.myProfileImgName != null) {
            this.myProfileImg = fileUploadUtil.getUserImg(this.myProfileImgName);
        }
        if (this.otherProfileImgName != null) {
            this.otherProfileImg = fileUploadUtil.getUserImg(this.otherProfileImgName);
        }
    }

    public void loadSongImg(FileUploadUtil fileUploadUtil) {
        if (this.songImgName != null) {
            this.songImg = fileUploadUtil.getSongImgByFileName(this.songImgName);
        }
    }


}
