package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.DifficultyRatingRequest;

public interface DifficultyRatingService {

    Long saveDifficultyRating(DifficultyRatingRequest request);

    Long updateDifficultyRating(Long id, DifficultyRatingRequest request);

    Long deleteStarRate(Long id);

    int findDifficultyRating(Long id);

}
