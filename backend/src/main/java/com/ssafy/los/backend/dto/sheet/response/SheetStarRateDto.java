package com.ssafy.los.backend.dto.sheet.response;

import com.ssafy.los.backend.domain.entity.SheetStarRate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SheetStarRateDto {

    private String nickname;
    private String userImg;
    private String content;
    private int starRate;
    private LocalDateTime createdAt;

    public static SheetStarRateDto toEntity(SheetStarRate sheetStarRate) {
        return SheetStarRateDto.builder()
                .nickname(sheetStarRate.getUser().getNickname())
                .userImg(sheetStarRate.getUser().getUserImg())
                .content(sheetStarRate.getContent())
                .starRate(sheetStarRate.getStarRate())
                .createdAt(sheetStarRate.getCreatedAt())
                .build();
    }

}
