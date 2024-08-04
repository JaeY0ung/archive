package com.ssafy.los.backend.dto.play;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlayerReadyDto {

    private String sender;
    private String isReady;
    private String profileImg;


    // 빌더 패턴
    @Builder
    public PlayerReadyDto(String sender, String isReady, String profileImg) {
        this.sender = sender;
        this.isReady = isReady;
        this.profileImg = profileImg;
    }
}
