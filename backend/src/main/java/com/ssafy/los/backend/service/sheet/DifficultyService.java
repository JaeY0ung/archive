package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.dto.sheet.request.DifficultyCreateDto;
import com.ssafy.los.backend.dto.sheet.request.DifficultyUpdateDto;
import com.ssafy.los.backend.dto.sheet.response.DifficultyResponseDto;
import java.util.List;

public interface DifficultyService {

    Long saveDifficulty(Long sheetId, Long userId, DifficultyCreateDto difficultyCreateDto);

    Long updateDifficulty(Long id, DifficultyUpdateDto difficultyUpdateDto);

    Long deleteDifficulty(Long difficultyId);

    List<DifficultyResponseDto> searchDifficultyBySheetId(Long sheetId);

    int calculateDifficulty(Long sheetId);
}