package com.ssafy.los.backend.dto.sheet.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SheetStarRateUpdateDto {

    private String content;
    private int starRate;

}
