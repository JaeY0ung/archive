package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateCreateDto;
import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateUpdateDto;
import com.ssafy.los.backend.sheet.model.dto.response.SheetStarRateResponseDto;
import com.ssafy.los.backend.sheet.model.entity.SheetStarRate;
import java.util.List;

public interface SheetStarRateService {

    Long saveStarRate(SheetStarRateCreateDto sheetStarRateCreateDto, Long sheetId);

    Long updateStarRate(Long sheetStarSheetStarId, SheetStarRateUpdateDto sheetStarRateUpdateDto);

    Long deleteStarRate(Long id);

    SheetStarRate findStarRateById(Long id);

    List<SheetStarRateResponseDto> findStarRateBySheetId(Long sheetId);


}
