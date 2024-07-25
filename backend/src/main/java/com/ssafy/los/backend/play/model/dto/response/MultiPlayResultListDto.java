package com.ssafy.los.backend.play.model.dto.response;

import com.ssafy.los.backend.play.model.entity.MultiPlayResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiPlayResultListDto {
    private String myUsername;
    private String myProfileUrl;
    private Float myScore;
    private String otherUsername;
    private String otherProfileUrl;
    private Float otherScore;
    private String sheetTitle;
    private String sheetUrl;
    private Integer level;
    private long playTime; // 플레이 시각을 초 단위로 저장
    private Boolean draw;

}
