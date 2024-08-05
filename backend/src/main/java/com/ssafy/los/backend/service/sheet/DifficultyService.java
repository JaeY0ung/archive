package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.dto.sheet.request.DifficultyCreateDto;
import com.ssafy.los.backend.dto.sheet.request.DifficultyUpdateDto;
import com.ssafy.los.backend.dto.sheet.response.DifficultyResponseDto;
import org.springframework.data.domain.Page;

public interface DifficultyService {

    Long saveDifficulty(Long sheetId, Long userId, DifficultyCreateDto difficultyCreateDto);

    Long updateDifficulty(Long id, DifficultyUpdateDto difficultyUpdateDto);

    Long deleteDifficulty(Long difficultyId);

    Page<DifficultyResponseDto> searchDifficultyBySheetId(Long sheetId, int page, int size);

    int calculateDifficulty(Long sheetId);
}