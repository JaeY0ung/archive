package com.ssafy.los.backend.dto.play;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlayerStartDto {

    private String type;
    private String sender;
    private String content;

    public PlayerStartDto() {
    }

    @Builder
    public PlayerStartDto(String type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

}
