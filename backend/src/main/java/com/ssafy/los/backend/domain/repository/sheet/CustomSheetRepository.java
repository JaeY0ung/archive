package com.ssafy.los.backend.domain.repository.sheet;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailForUserDto;

import java.util.List;

public interface CustomSheetRepository {

    List<SheetDetailDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter);

    List<SheetDetailDto> findSheetsByFilter(SheetSearchFilter sheetSearchFilter,
            User loginUser);

    List<SheetDetailDto> findSheetsByStatusForAdmin(Integer status);

    SheetDetailDto findSheetDetailViewDtoById(Long sheetId);

    SheetDetailDto findSheetDetailViewDtoById(Long sheetId, User loginUser);

    long updateViewCount(Long sheetId);

    List<SheetDetailForUserDto> searchByFileName(List<String> fileNames);

    List<SheetDetailForUserDto> searchByUserLike(Long userId);

}
