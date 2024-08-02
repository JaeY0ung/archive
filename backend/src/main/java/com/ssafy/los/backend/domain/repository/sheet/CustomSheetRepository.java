package com.ssafy.los.backend.domain.repository.sheet;

import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailViewDto;
import java.util.List;

public interface CustomSheetRepository {

    List<SheetDetailViewDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter);

    SheetDetailViewDto findSheetDetailViewDtoById(Long sheetId, Long userId);

    long updateViewCount(Long sheetId);

}
