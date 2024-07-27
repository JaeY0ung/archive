package com.ssafy.los.backend.play.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlayerReadyDto {
    private String sender;
    private String isReady;

    // 기본 생성자
    public PlayerReadyDto() {
    }

    // 빌더 패턴
    @Builder
    public PlayerReadyDto(String sender, String isReady) {
        this.sender = sender;
        this.isReady = isReady;
    }
}
