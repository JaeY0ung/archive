package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.DifficultyCreateDto;
import com.ssafy.los.backend.sheet.model.dto.request.DifficultyUpdateDto;
import com.ssafy.los.backend.sheet.model.dto.response.DifficultyResponseDto;
import java.util.List;

public interface DifficultyService {

    Long saveDifficulty(Long sheetId, Long userId, DifficultyCreateDto difficultyCreateDto);

    Long updateDifficulty(Long id, DifficultyUpdateDto difficultyUpdateDto);

    Long deleteDifficulty(Long difficultyId);

    List<DifficultyResponseDto> searchDifficultyBySheetId(Long sheetId);

    int calculateDifficulty(Long sheetId);
}