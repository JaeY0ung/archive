package com.ssafy.los.backend.dto.sheet.response;

import com.ssafy.los.backend.domain.entity.SheetStarRate;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SheetStarRateDto {

    private String nickname;
    private String userImgName;
    private String userImg;
    private String content;
    private int starRate;
    private LocalDateTime createdAt;

    public static SheetStarRateDto toEntity(SheetStarRate sheetStarRate) {
        return SheetStarRateDto.builder()
                .nickname(sheetStarRate.getUser().getNickname())
                .userImgName(sheetStarRate.getUser().getUserImg())
                .content(sheetStarRate.getContent())
                .starRate(sheetStarRate.getStarRate())
                .createdAt(sheetStarRate.getCreatedAt())
                .build();
    }

    public void loadUserImg(FileUploadUtil fileUploadUtil) {
        if (this.userImgName != null) {
            this.userImg = fileUploadUtil.getUserImg(this.userImgName);
        }
    }
}
