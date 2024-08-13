package com.ssafy.los.backend.domain.repository.sheet;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailForUserDto;
import java.util.List;

public interface CustomSheetRepository {

    List<SheetDetailDto> findSheetsByFilterAndLoginUser(SheetSearchFilter sheetSearchFilter,
            User loginUser);

    List<SheetDetailDto> findSheetsByStatusForAdmin(SheetSearchFilter sheetSearchFilter);

    SheetDetailDto findSheetDetailViewDtoById(Long sheetId, User loginUser);

    SheetDetailDto searchOneRecentSinglePlayedSheet(User loginUser);

    long updateViewCount(Long sheetId);

    List<SheetDetailForUserDto> searchByFileNames(List<String> fileNames);


    List<SheetDetailForUserDto> searchByUserLike(Long userId);

}
