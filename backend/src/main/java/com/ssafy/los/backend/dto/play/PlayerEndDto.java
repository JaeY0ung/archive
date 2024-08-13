package com.ssafy.los.backend.dto.play;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerEndDto {

    private String sender;
    private Integer score;
    private Integer multiResultId;

}
