package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetUploadForm;
import com.ssafy.los.backend.sheet.model.dto.response.SheetResponseDto;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.repository.SheetRepository;
import com.ssafy.los.backend.song.model.repository.SongRepository;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
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
    public SheetResponseDto searchSheetById(Long sheetId) {
        return toSheetResponseDto(sheetRepository.findById(sheetId).orElseThrow(
                IllegalArgumentException::new));
    }

    @Override
    public Resource getSheetFileByName(String fileName) throws IOException {
        return fileUploadUtil.downloadSheetFile(fileName);
    }

    @Override
    public List<SheetResponseDto> searchSheetListBySort(String sort) {
        List<Sheet> sheetList = null;
        if (sort.equals("new")) {  // sort : new
            sheetList = sheetRepository.findByDeletedAtIsNullAndCreatedAtIsNotNullOrderByCreatedAtDesc();
        } else if (sort.equals("popular")) { // sort : popular
            // TODO : 메서드 고쳐야 함
            sheetList = sheetRepository.findByDeletedAtIsNullAndCreatedAtIsNotNullOrderByCreatedAtDesc();
        }

        return sheetList.stream().map(this::toSheetResponseDto)
                .collect(Collectors.toList());
    }

    public SheetResponseDto toSheetResponseDto(Sheet sheet) {
        String base64SongImg = null;
        try {
            log.info("fileUploadUtil.getSomgImgPath(sheet.getFileName())"
                    + fileUploadUtil.getSomgImgPath(sheet.getFileName()));
            byte[] songImg = Files.readAllBytes(
                    fileUploadUtil.getSomgImgPath(sheet.getSong().getImgName()));
            base64SongImg = Base64.getEncoder().encodeToString(songImg);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return SheetResponseDto.builder()
                .id(sheet.getId())
                .title(sheet.getTitle())
                .songTitle(sheet.getSong().getTitle())
                .songComposer(sheet.getSong().getComposer())
                .uploaderNickname(sheet.getUploader().getNickname())
                .songImg(base64SongImg)
                .price(sheet.getPrice())
                .level(sheet.getLevel())
                .point(sheet.getPoint())
                .viewCount(sheet.getViewCount())
                .build();
    }
}