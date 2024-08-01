package com.ssafy.los.backend.sheet.model.repository.custom;

import com.ssafy.los.backend.sheet.model.dto.request.SheetSearchFilter;
import com.ssafy.los.backend.sheet.model.dto.response.SheetDetailViewDto;
import java.util.List;

public interface CustomSheetRepository {

    List<SheetDetailViewDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter);

    SheetDetailViewDto findSheetDetailViewDtoById(Long sheetId, Long userId);

}
