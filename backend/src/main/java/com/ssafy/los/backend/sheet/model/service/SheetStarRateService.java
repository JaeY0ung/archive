package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateCreateDto;
import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateUpdateDto;
import com.ssafy.los.backend.sheet.model.dto.response.SheetStarRateDto;
import java.util.List;

public interface SheetStarRateService {

    Long saveSheetStarRate(SheetStarRateCreateDto sheetStarRateCreateDto, Long sheetId);

    Long updateSheetStarRate(Long sheetStarSheetStarId, SheetStarRateUpdateDto sheetStarRateUpdateDto);

    Long deleteSheetStarRate(Long id);

    List<SheetStarRateDto> searchSheetStarRateBySheetId(Long sheetId);

}
