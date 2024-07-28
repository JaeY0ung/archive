package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetUploadForm;
import com.ssafy.los.backend.sheet.model.dto.response.SheetResponseDto;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.user.model.entity.User;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface SheetService {

    String saveSheetFile(MultipartFile file) throws IOException;

    Sheet saveSheetInfo(SheetUploadForm sheetUploadForm, User loginUser, String fileName);

    Sheet selectById(Long sheetId);

    SheetResponseDto searchSheetById(Long sheetId);

    Resource getSheetFileByName(String fileName) throws IOException;

    List<SheetResponseDto> searchSheetByFilter(String keyword, String sort);
}
