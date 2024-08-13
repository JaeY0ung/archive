package com.ssafy.los.backend.dto.play;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlayerStartDto {
    private String type;
    private String sender;
    private String content;
    private Integer sheetId;
    private Integer resultId;
}
