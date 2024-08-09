package com.ssafy.los.backend.dto.play;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStartDto {
    private String type;
    private String sender;
    private String content;
    private Integer sheetId;
}
