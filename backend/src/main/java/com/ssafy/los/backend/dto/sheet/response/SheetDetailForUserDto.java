package com.ssafy.los.backend.dto.sheet.response;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.sql.Timestamp;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@ToString
public class SheetDetailForUserDto implements SheetDetailDto {

    private Long id;

    private String title;

    private String songTitle;

    private String songComposer;

    private String uploaderNickname;

    private String uuid;

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

    public SheetDetailForUserDto(Sheet sheet, Long likeCount, Boolean likeStatus) {
        this.id = sheet.getId();
        this.title = sheet.getTitle();
        if (sheet.getSong() != null) {
            this.songTitle = sheet.getSong().getTitle();
            this.songComposer = sheet.getSong().getComposer();
            this.songImgName = sheet.getSong().getImgName();
        }
        if (sheet.getUploader() != null) {
            this.uploaderNickname = sheet.getUploader().getNickname();
        }
        this.uuid = sheet.getUuid();
        this.price = sheet.getPrice();
        this.level = sheet.getLevel();
        this.status = sheet.getStatus();
        if (sheet.getStatus() != null && sheet.getStatus() == 0) {
            this.level = 0;
        }
        this.viewCount = sheet.getViewCount();
        this.likeCount = likeCount;
        this.likeStatus = likeStatus;
    }

    public SheetDetailForUserDto(Sheet sheet, Long likeCount) {
        this(sheet, likeCount, null);
    }

    @Override
    public void loadSongImg(FileUploadUtil fileUploadUtil) {
        if (this.songImgName != null) {
            this.songImg = fileUploadUtil.getSongImgByFileName(this.songImgName);
        }
    }

}
