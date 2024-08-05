package com.ssafy.los.backend.dto.sheet.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DifficultyUpdateDto {
    private Integer level;
    private String contents;
}
