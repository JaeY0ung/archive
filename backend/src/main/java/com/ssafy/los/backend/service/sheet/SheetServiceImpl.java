package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.domain.repository.song.SongRepository;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.request.SheetUploadForm;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailViewDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public Sheet registerSheetAndFile(SheetUploadForm sheetUploadForm)
            throws IllegalArgumentException {
        // TODO : mid -> mp3 변환한 파일 추가로 저장하는 로직 구현하기
        return registerSheet(sheetUploadForm, saveSheetFile(sheetUploadForm.getFile()));
    }

    @Override
    public Sheet searchById(Long sheetId) throws IllegalArgumentException {
        return sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 악보입니다."));
    }

    @Transactional
    @Override
    public SheetDetailViewDto searchSheetById(Long sheetId) throws IllegalArgumentException {
        sheetRepository.updateViewCount(sheetId);
        SheetDetailViewDto sheet = sheetRepository.findSheetDetailViewDtoById(sheetId,
                authService.getLoginUser() != null ? authService.getLoginUser().getId() : null);
        sheet.loadSongImg(fileUploadUtil);
        return sheet;

    }

    @Override
    public Resource getSheetFileByFileName(String fileName) throws IllegalArgumentException {
        return fileUploadUtil.downloadSheetFile(fileName);
    }

    @Override
    public List<SheetDetailViewDto> searchSheetByFilter(SheetSearchFilter sheetSearchFilter) {
        return sheetRepository.findSheetsByFilter(sheetSearchFilter)
                .stream()
                .peek(dto -> dto.loadSongImg(fileUploadUtil))
                .toList();
    }

    private String saveSheetFile(MultipartFile file) throws IllegalArgumentException {
        return fileUploadUtil.uploadSheet(file); // 로컬에 저장
    }

    private Sheet registerSheet(SheetUploadForm sheetUploadForm, String fileName)
            throws IllegalArgumentException {
        try {
            Sheet sheet = Sheet.builder()
                    .uploader(authService.getLoginUser())
                    .level(sheetUploadForm.getLevel())
                    .title(sheetUploadForm.getTitle())
                    .song(songRepository.findById(sheetUploadForm.getSongId()).orElse(null))
                    .fileName(fileName)
                    .build();
            return sheetRepository.save(sheet);
        } catch (Exception e) {
            throw new IllegalArgumentException("악보 저장 실패");
        }
    }

}