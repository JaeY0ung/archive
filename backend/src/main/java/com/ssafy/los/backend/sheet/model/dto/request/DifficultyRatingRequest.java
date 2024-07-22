package com.ssafy.los.backend.sheet.model.dto.request;

import com.ssafy.los.backend.sheet.model.entity.DifficultyRating;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DifficultyRatingRequest {

    private Long userId;
    private Long sheetId;
    private Integer level;
    private String contents;

    @Builder
    public DifficultyRating toEntity(User user, Sheet sheet) {
        return DifficultyRating.builder()
                .user(user)
                .sheet(sheet)
                .level(level)
                .contents(contents)
                .build();
    }
}
