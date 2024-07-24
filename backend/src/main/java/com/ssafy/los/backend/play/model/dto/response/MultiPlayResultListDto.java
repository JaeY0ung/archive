package com.ssafy.los.backend.play.model.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MultiPlayResultListDto {
    private String myUsername;
    private String myProfileUrl;
    private Integer myScore;
    private String otherUsername;
    private String otherProfileUrl;
    private Integer otherScore;
    private String sheetTitle;
    private String sheetUrl;
    private Integer level;
//    private LocalDateTime playTime;

}
