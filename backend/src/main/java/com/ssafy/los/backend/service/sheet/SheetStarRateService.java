package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.dto.sheet.request.SheetStarRateCreateDto;
import com.ssafy.los.backend.dto.sheet.request.SheetStarRateUpdateDto;
import com.ssafy.los.backend.dto.sheet.response.SheetStarRateDto;
import java.util.List;

public interface SheetStarRateService {

    Long saveSheetStarRate(SheetStarRateCreateDto sheetStarRateCreateDto, Long sheetId);

    Long updateSheetStarRate(Long sheetStarSheetStarId,
            SheetStarRateUpdateDto sheetStarRateUpdateDto);

    Long deleteSheetStarRate(Long id);

    List<SheetStarRateDto> searchSheetStarRateBySheetId(Long sheetId);

}
