package com.ssafy.los.backend.play.model.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiPlayResultAfterDto {
    private Long myUserId;
    private Float myScore;
    private Long otherUserId;
    private Float otherScore;


}