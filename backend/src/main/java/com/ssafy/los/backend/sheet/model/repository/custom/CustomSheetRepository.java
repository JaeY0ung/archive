package com.ssafy.los.backend.sheet.model.repository.custom;

import com.ssafy.los.backend.sheet.model.dto.response.SheetDetailViewDto;
import java.util.List;

public interface CustomSheetRepository {

    List<SheetDetailViewDto> findSheets(String keyword, String sort, Long userId);

    SheetDetailViewDto findSheetDetailViewDtoById(Long sheetId, Long userId);

    List<SheetDetailViewDto> findSheetsByLevelRandomly(Integer level, Long userId);

}
