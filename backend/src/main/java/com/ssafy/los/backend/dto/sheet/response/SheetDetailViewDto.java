package com.ssafy.los.backend.dto.sheet.response;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j

public class SheetDetailViewDto {

    private Long id;

    private String title;

    private String songTitle;

    private String songComposer;

    private String uploaderNickname;

    private String fileName;

    private String songImgName;

    private String songImg;

    private Integer price;

    private Integer level; // 1,2,3,4,5,6,7,8,9

    private Integer status; // "waiting: 0, accepted: 1, rejected: 2"

    private Integer point;

    private Integer viewCount;

    private Boolean likeStatus; // 로그인 유저가 좋아하면 true, 아니면 false, 로그인 안 했으면 null

    private Long likeCount; // 좋아요 수

    private Timestamp createdAt;

    public SheetDetailViewDto(Sheet sheet, Long likeCount, Boolean likeStatus) {
        this.id = sheet.getId();
        this.title = sheet.getTitle();
        if (sheet.getSong() != null) {
            this.songTitle = sheet.getSong().getTitle();
            this.songComposer = sheet.getSong().getComposer();
            this.uploaderNickname = sheet.getUploader().getNickname();
            this.songImgName = sheet.getSong().getImgName();
        }

        this.fileName = sheet.getFileName();
        this.price = sheet.getPrice();
        this.level = sheet.getLevel();
        this.status = sheet.getStatus();
        this.viewCount = sheet.getViewCount();
        this.likeCount = likeCount;
        this.likeStatus = likeStatus;
    }

    public SheetDetailViewDto(Sheet sheet, Long likeCount) {
        this(sheet, likeCount, null);
    }

    public void loadSongImg(FileUploadUtil fileUploadUtil) {
        if (this.songImgName != null) {
            this.songImg = fileUploadUtil.getImg(this.songImgName);
        }
    }

}
