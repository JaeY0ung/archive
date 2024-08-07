package com.ssafy.los.backend.dto.sheet.response;

import com.ssafy.los.backend.domain.entity.Difficulty;
import java.time.LocalDateTime;

import com.ssafy.los.backend.util.FileUploadUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DifficultyResponseDto {

    private Long difficultyId;
    private String username;
    private String userImgName;
    private String userImg;
    private Integer singleScore;
    private String sheetTitle;
    private String sheetFileName;
    private int level;
    private String content;
    private LocalDateTime createdAt;

    public static DifficultyResponseDto toEntity(Difficulty difficulty, FileUploadUtil fileUploadUtil) {
        DifficultyResponseDto dto = DifficultyResponseDto.builder()
                .difficultyId(difficulty.getId())
                .username(difficulty.getUser().getNickname())
                .userImgName(difficulty.getUser().getUserImg())
                .singleScore(difficulty.getUser().getSingleScore())
                .sheetTitle(difficulty.getSheet().getTitle())
                .sheetFileName(difficulty.getSheet().getFileName())
                .level(difficulty.getLevel())
                .content(difficulty.getContent())
                .createdAt(difficulty.getCreatedAt())
                .build();

        dto.loadUserImg(fileUploadUtil);
        return dto;
    }

    public void loadUserImg(FileUploadUtil fileUploadUtil) {
        if (this.userImgName != null) {
            this.userImg = fileUploadUtil.getUserImg(this.userImgName);
        }
    }

}
