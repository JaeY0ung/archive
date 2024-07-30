package com.ssafy.los.backend.sheet.model.repository.custom;

import com.ssafy.los.backend.sheet.model.dto.response.SheetResponseDto;
import java.util.List;

public interface CustomSheetRepository {

    List<SheetResponseDto> findSheets(String keyword, String sort, Long userId);

    SheetResponseDto findSheetById(Long sheetId, Long userId);

    List<SheetResponseDto> findSheetsByLevelRandomly(Integer level, Long userId);

}
