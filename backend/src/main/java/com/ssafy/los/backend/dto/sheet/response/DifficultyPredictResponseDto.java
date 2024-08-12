package com.ssafy.los.backend.dto.sheet.response;

import lombok.Data;

@Data
public class DifficultyPredictResponseDto {

    private int predicted_difficulty;
    private int prediction_confidence;
}
