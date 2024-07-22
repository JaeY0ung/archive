package com.ssafy.los.backend.sheet.model.dto.response;

import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.entity.SheetStarRate;
import com.ssafy.los.backend.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SheetStarRateResponse {

    private User user; // 아이디로 하는게 맞나?
    private Sheet sheet;
    private String content;
    private int starRate;

    @Builder
    public SheetStarRateResponse(User user, Sheet sheet, String content, int rate) {
        this.user = user;
        this.sheet = sheet;
        this.content = content;
        this.starRate = starRate;
    }

    public SheetStarRate toEntity() {
        return SheetStarRate.builder()
                .user(user)
                .sheet(sheet)
                .content(content)
                .starRate(starRate)
                .build();
    }
}
