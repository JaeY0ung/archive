package com.ssafy.los.backend.user.model.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class ScoreDto {
    private String nickname;
    private String userImg;
    private Integer score;


}
