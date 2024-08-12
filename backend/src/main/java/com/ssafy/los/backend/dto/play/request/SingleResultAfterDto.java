package com.ssafy.los.backend.dto.play.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SingleResultAfterDto {

    private Long userId;
    private Float score;
}
