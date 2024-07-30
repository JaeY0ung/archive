package com.ssafy.los.backend.play.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlayerReadyDto {
    private String senderNickname;
    private String isReady;
    private String profileImg;


    // 빌더 패턴
    @Builder
    public PlayerReadyDto(String senderNickname, String isReady, String profileImg) {
        this.senderNickname = senderNickname;
        this.isReady = isReady;
        this.profileImg = profileImg;
    }
}
