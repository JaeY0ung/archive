package com.ssafy.los.backend.dto.sheet.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DifficultyCreateDto {

    private Integer level;
    private String content;
}
