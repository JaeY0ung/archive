package com.ssafy.los.backend.sheet.model.dto.response;

import com.ssafy.los.backend.sheet.model.entity.SheetStarRate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SheetStarRateResponseDto {

    private String nickname;
    private String userImg;
    private String content;
    private int starRate;
    private LocalDateTime createdAt;

    public static SheetStarRateResponseDto toEntity(SheetStarRate sheetStarRate) {
        return SheetStarRateResponseDto.builder()
                .nickname(sheetStarRate.getUser().getNickname())
                .userImg(sheetStarRate.getUser().getUserImg())
                .content(sheetStarRate.getContent())
                .starRate(sheetStarRate.getStarRate())
                .createdAt(sheetStarRate.getCreatedAt())
                .build();
    }

}
