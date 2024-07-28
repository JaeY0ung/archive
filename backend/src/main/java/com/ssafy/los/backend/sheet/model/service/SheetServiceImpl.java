package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetUploadForm;
import com.ssafy.los.backend.sheet.model.dto.response.SheetResponseDto;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.repository.SheetRepository;
import com.ssafy.los.backend.song.model.repository.SongRepository;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.service.AuthService;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final AuthService authService;


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
    public Sheet selectById(Long sheetId) {
        return sheetRepository.findById(sheetId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 악보입니다."));
    }

    @Override
    public SheetResponseDto searchSheetById(Long sheetId) {
        Long loginUserId;
        try {
            loginUserId = authService.getLoginUser().getId();

        } catch (Exception e) {
            loginUserId = null;
        }
        return addSongImg(sheetRepository.findSheetById(sheetId, loginUserId));

    }

    @Override
    public Resource getSheetFileByName(String fileName) throws IOException {
        return fileUploadUtil.downloadSheetFile(fileName);
    }

    @Override
    public List<SheetResponseDto> searchSheetByFilter(String keyword, String sort) {
        Long loginUserId;
        try {
            loginUserId = authService.getLoginUser().getId();
        } catch (Exception e) {
            loginUserId = null;
        }
        return sheetRepository.findSheets(keyword, sort, loginUserId)
                .stream().map(this::addSongImg).
                collect(Collectors.toList());
    }

    public SheetResponseDto addSongImg(SheetResponseDto sheetResponseDto) {
        try {
            Path path = fileUploadUtil.getSomgImgPath(sheetResponseDto.getSongImgName());
            byte[] songImg = Files.readAllBytes(path);
            String base64SongImg = Base64.getEncoder().encodeToString(songImg);
            sheetResponseDto.setSongImg(base64SongImg);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return sheetResponseDto;
    }
}