package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateRequest;
import com.ssafy.los.backend.sheet.model.entity.SheetStarRate;
import java.util.List;

public interface SheetStarRateService {

    Long saveStarRate(SheetStarRateRequest requestDto);

    Long updateStarRate(Long id, SheetStarRateRequest requestDto);

    Long deleteStarRate(Long id);

    SheetStarRate findStarRateById(Long id);

    List<SheetStarRate> findStarRateBySheetId(Long sheetId);


}
