package com.ssafy.los.backend.dto.user.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScoreDto {

    private String nickname;
    private Integer f1Score;
    private Integer jaccardScore;


}
