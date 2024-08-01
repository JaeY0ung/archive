package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetSearchFilter;
import com.ssafy.los.backend.sheet.model.dto.request.SheetUploadForm;
import com.ssafy.los.backend.sheet.model.dto.response.SheetDetailViewDto;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
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
}
