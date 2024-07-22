package com.ssafy.los.backend.sheet.model.dto.request;

import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.entity.SheetStarRate;
import com.ssafy.los.backend.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SheetStarRateRequest {

    private Long userId;
    private Long sheetId;
    private String content;
    private int starRate;

    @Builder
    public SheetStarRate toEntity(User user, Sheet sheet) {
        return SheetStarRate.builder()
                .user(user)
                .sheet(sheet)
                .content(content)
                .starRate(starRate)
                .build();
    }

}
