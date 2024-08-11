package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.request.SheetUpdateFormDto;
import com.ssafy.los.backend.dto.sheet.request.SheetUploadForm;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailForUserDto;
import java.util.List;
import org.springframework.core.io.Resource;

public interface SheetService {

    Long registerSheetAndMidFileAndSplit(SheetUploadForm sheetUploadForm)
            throws IllegalArgumentException;

    Sheet searchById(Long sheetId) throws IllegalArgumentException;

    SheetDetailDto searchSheetDetailById(Long sheetId) throws IllegalArgumentException;

    Resource getSheetFileByFileName(String fileName) throws IllegalArgumentException;

    List<SheetDetailDto> searchSheetByFilter(SheetSearchFilter sheetSearchFilter)
            throws IllegalArgumentException;

    String getMusicXmlFileById(Long sheetId);

    String getMidFileById(Long sheetId);

    boolean deleteSheet(Long sheetId);

    void changeStatusByStatus(Long sheetId, Integer status);

    List<SheetDetailDto> searchAllSheetsByStatusForAdmin(SheetSearchFilter sheetSearchFilter);

    Sheet updateSheet(Long sheetId, SheetUpdateFormDto sheetUpdateFormDto);

    Sheet searchSheetPlayLatest();

    List<SheetDetailForUserDto> searchSheetDetailByFileName(List<String> fileNames);

    List<SheetDetailForUserDto> getRecommendedSheets();

    List<SheetDetailForUserDto> searchSheetByUserLike(Long userId);
}
