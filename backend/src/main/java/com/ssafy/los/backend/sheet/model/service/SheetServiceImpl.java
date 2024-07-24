package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetUploadForm;
import com.ssafy.los.backend.sheet.model.dto.response.SheetResponseDto;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.repository.SheetRepository;
import com.ssafy.los.backend.song.model.repository.SongRepository;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class SheetServiceImpl implements SheetService {

    private final FileUploadUtil fileUploadUtil;
    private final SheetRepository sheetRepository;
    private final SongRepository songRepository;


    public String saveSheetFile(MultipartFile file) throws IOException {
        return fileUploadUtil.uploadSheet(file); // 로컬에 저장
    }

    public Sheet saveSheetInfo(SheetUploadForm sheetUploadForm, User loginUser, String fileName) {
        Sheet sheet = Sheet.builder()
                .uploader(loginUser)
                .level(sheetUploadForm.getLevel())
                .point(sheetUploadForm.getPoint()).title(sheetUploadForm.getTitle())
                .song(songRepository.findById(sheetUploadForm.getSongId()).orElse(null))
                .price(sheetUploadForm.getPrice())
                .fileName(fileName)
                .build();

        return sheetRepository.save(sheet);
    }

    @Override
    public Sheet searchSheetById(Long sheetId) {
        return sheetRepository.findById(sheetId).orElse(null);
    }

    @Override
    public Resource getSheetFileByName(String fileName) throws IOException {
        return fileUploadUtil.downloadSheet(fileName);
    }

    @Override
    public List<SheetResponseDto> searchSheetListBySort(String sort) {
        if (sort.equals("new")) {  // sort : new
            return sheetRepository.findSheetResponseDtoListByDeletedAtIsNullAndCreatedAtIsNotNullOrderByCreatedAt();
        }
        if (sort.equals("popular")) { // sort : popular
            // 메서드 고쳐야 함
            return sheetRepository.findSheetResponseDtoListByDeletedAtIsNullAndCreatedAtIsNotNullOrderByCreatedAt();
        }
        return null;
    }
}