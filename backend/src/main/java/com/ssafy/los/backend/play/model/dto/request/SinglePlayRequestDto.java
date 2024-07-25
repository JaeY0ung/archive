package com.ssafy.los.backend.play.model.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SinglePlayRequestDto {
    private Long sheetId;
    private Float score;
}
