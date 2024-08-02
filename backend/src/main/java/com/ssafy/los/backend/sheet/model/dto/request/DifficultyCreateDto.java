package com.ssafy.los.backend.sheet.model.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DifficultyCreateDto {
    private Integer level;
    private String contents;
}
