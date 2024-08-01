package com.ssafy.los.backend.sheet.model.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DifficultyCreateRequestDto {
    private Integer level;
    private String contents;
}
