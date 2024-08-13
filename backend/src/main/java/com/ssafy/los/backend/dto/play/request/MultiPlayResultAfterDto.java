package com.ssafy.los.backend.dto.play.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiPlayResultAfterDto {

    private Long myUserId;
    private Float myScore;
    private String otherUserId;
    private Float otherScore;

}
