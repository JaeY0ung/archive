package com.ssafy.los.backend.play.model.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SingleRequestDto {
    private Long sheetId;
    private Float score;
}
