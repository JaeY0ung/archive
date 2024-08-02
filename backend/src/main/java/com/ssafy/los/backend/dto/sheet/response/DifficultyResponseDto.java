package com.ssafy.los.backend.dto.sheet.response;

import com.ssafy.los.backend.domain.entity.Difficulty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DifficultyResponseDto {

    private String username;
    private String userImg;
    private String sheetTitle;
    private String sheetFileName;
    private int level;
    private String content;
    private LocalDateTime createAt;

    public static DifficultyResponseDto toEntity(Difficulty difficulty) {
        return DifficultyResponseDto.builder()
                .username(difficulty.getUser().getNickname())
                .userImg(difficulty.getUser().getUserImg())
                .sheetTitle(difficulty.getSheet().getTitle())
                .sheetFileName(difficulty.getSheet().getFileName())
                .level(difficulty.getLevel())
                .content(difficulty.getContent())
                .createAt(difficulty.getCreatedAt())
                .build();
    }

}
