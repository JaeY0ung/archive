package com.ssafy.los.backend.play.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiPlayScoreDto {

    private int score;

    public int aggregateScore(int num) {
        this.score += num;
        return score;
    }

}
