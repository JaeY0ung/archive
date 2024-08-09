package com.ssafy.los.backend.domain.repository.sheet;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import java.util.List;

public interface CustomSheetRepository {

    List<SheetDetailDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter);

    List<SheetDetailDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter,
            User loginUser);


    SheetDetailDto findSheetDetailViewDtoById(Long sheetId);

    SheetDetailDto findSheetDetailViewDtoById(Long sheetId, User loginUser);

    long updateViewCount(Long sheetId);

}
