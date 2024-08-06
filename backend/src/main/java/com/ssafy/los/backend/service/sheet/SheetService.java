package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.request.SheetUploadForm;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailViewDto;
import java.util.List;
import org.springframework.core.io.Resource;

public interface SheetService {

    Sheet registerSheetAndFile(SheetUploadForm sheetUploadForm)
            throws IllegalArgumentException;

    Sheet searchById(Long sheetId) throws IllegalArgumentException;

    SheetDetailViewDto searchSheetById(Long sheetId) throws IllegalArgumentException;

    Resource getSheetFileByFileName(String fileName) throws IllegalArgumentException;

    List<SheetDetailViewDto> searchSheetByFilter(SheetSearchFilter sheetSearchFilter)
            throws IllegalArgumentException;

    String getMusicXmlFileById(Long sheetId);

    String getMidFileById(Long sheetId);
}
