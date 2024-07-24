package com.ssafy.los.backend.sheet.model.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SheetStarRateCreateDto {

    private Long sheetId;
    private String content;
    private int starRate;

}
