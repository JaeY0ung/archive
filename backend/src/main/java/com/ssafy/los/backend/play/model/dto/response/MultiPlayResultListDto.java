package com.ssafy.los.backend.play.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiPlayResultListDto {

    private String myNickname;

    private String myProfileImg;

    private Float myScore;

    private String otherNickname;

    private String otherProfileImg;

    private Float otherScore;

    private String sheetTitle;

    private String sheetUrl;

    private Integer level;

    private long playTime; // 플레이 시각을 초 단위로 저장

    private Boolean draw;


}
