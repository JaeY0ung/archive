package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetUploadForm;
import com.ssafy.los.backend.sheet.model.dto.response.SheetDetailViewDto;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface SheetService {

    Sheet registerSheetAndFile(MultipartFile file, SheetUploadForm sheetUploadForm)
            throws IllegalArgumentException;

    Sheet searchById(Long sheetId);

    SheetDetailViewDto searchSheetById(Long sheetId);

    Resource getSheetFileByName(String fileName) throws IllegalArgumentException;

    List<SheetDetailViewDto> searchSheetByFilter(String keyword, String sort);

    List<SheetDetailViewDto> searchSheetByLevelRandomly(Integer level);
}
