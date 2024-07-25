package com.ssafy.los.backend.play.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiResultListDto {
    private String myUsername;
    private String myProfileUrl;
    private Float myScore;
    private String otherUsername;
    private String otherProfileUrl;
    private Float otherScore;
    private String sheetTitle;
    private String sheetUrl;
    private Integer level;
//    private LocalDateTime playTime;

}
