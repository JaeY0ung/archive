package com.ssafy.los.backend.dto.sheet.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DifficultyCreateDto {

    private Integer level;
    private String content;
}
