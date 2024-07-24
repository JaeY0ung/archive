package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.DifficultyCreateRequestDto;
import com.ssafy.los.backend.sheet.model.dto.request.DifficultyUpdateRequestDto;
import com.ssafy.los.backend.sheet.model.dto.response.DifficultyResponseDto;
import java.util.List;

public interface DifficultyService {

    Long saveDifficultyRating(Long sheetId, DifficultyCreateRequestDto difficultyCreateRequestDto);

    Long updateDifficultyRating(Long id, DifficultyUpdateRequestDto difficultyUpdateRequestDto);

    Long deleteDifficulty(Long difficultyId);

    List<DifficultyResponseDto> findDifficultyRating(Long sheetId);

    int calculateDifficulty(Long sheetId);

}
