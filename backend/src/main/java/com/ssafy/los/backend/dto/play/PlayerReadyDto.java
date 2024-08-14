package com.ssafy.los.backend.dto.play;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerReadyDto {

    private String sender;
    private String isReady;
    private String profileImg;

}
