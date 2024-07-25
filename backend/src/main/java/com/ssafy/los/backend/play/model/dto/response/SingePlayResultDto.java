package com.ssafy.los.backend.play.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SingePlayResultDto {
    private String nickname;
    private String userImg;
    private String sheetTitle;
    private String sheetUrl;
    private Integer level;
    private Integer point;
    private Long playTime;

}
