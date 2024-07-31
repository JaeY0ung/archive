package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetUploadForm;
import com.ssafy.los.backend.sheet.model.dto.response.SheetDetailViewDto;
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

    @Override
    public Sheet registerSheetAndFile(MultipartFile file, SheetUploadForm sheetUploadForm)
            throws IllegalArgumentException {
        // TODO : mid -> mp3 변환한 파일 추가로 저장하는 로직 구현하기
        return registerSheet(sheetUploadForm, saveSheetFile(file));
    }

    @Override
    public Sheet searchById(Long sheetId) throws IllegalArgumentException {
        return sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 악보입니다."));
    }

    @Override
    public SheetDetailViewDto searchSheetById(Long sheetId) {
        try {
            return addSongImg(sheetRepository.findSheetDetailViewDtoById(sheetId,
                    authService.getLoginUser().getId()));
        } catch (Exception e) {
            return addSongImg(sheetRepository.findSheetDetailViewDtoById(sheetId, null));
        }

    }

    @Override
    public Resource getSheetFileByName(String fileName) throws IllegalArgumentException {
        return fileUploadUtil.downloadSheetFile(fileName);
    }

    @Override
    public List<SheetDetailViewDto> searchSheetByFilter(String keyword, String sort) {
        try {
            return sheetRepository.findSheets(keyword, sort, authService.getLoginUser().getId())
                    .stream().map(this::addSongImg)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return sheetRepository.findSheets(keyword, sort, null)
                    .stream().map(this::addSongImg)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<SheetDetailViewDto> searchSheetByLevelRandomly(Integer level) {
        try {
            return sheetRepository.findSheetsByLevelRandomly(level,
                            authService.getLoginUser().getId())
                    .stream().map(this::addSongImg)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return sheetRepository.findSheetsByLevelRandomly(level, null)
                    .stream().map(this::addSongImg)
                    .collect(Collectors.toList());
        }
    }

    private String saveSheetFile(MultipartFile file) throws IllegalArgumentException {
        return fileUploadUtil.uploadSheet(file); // 로컬에 저장
    }

    private Sheet registerSheet(SheetUploadForm sheetUploadForm, String fileName)
            throws IllegalArgumentException {
        try {
            User loginUser = authService.getLoginUser();
            Sheet sheet = Sheet.builder()
                    .uploader(loginUser)
                    .level(sheetUploadForm.getLevel())
                    .title(sheetUploadForm.getTitle())
                    .song(songRepository.findById(sheetUploadForm.getSongId()).orElse(null))
                    .fileName(fileName)
                    .build();
            return sheetRepository.save(sheet);
        } catch (Exception e) {
            throw new IllegalArgumentException("저장 실패");
        }
    }

    public SheetDetailViewDto addSongImg(SheetDetailViewDto sheetDetailViewDto) {
        try {
            Path path = fileUploadUtil.getSomgImgPath(sheetDetailViewDto.getSongImgName());
            byte[] songImg = Files.readAllBytes(path);
            String base64SongImg = Base64.getEncoder().encodeToString(songImg);
            sheetDetailViewDto.setSongImg(base64SongImg);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return sheetDetailViewDto;
    }
}