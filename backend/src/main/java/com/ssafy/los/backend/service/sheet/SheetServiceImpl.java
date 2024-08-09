package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.domain.repository.song.SongRepository;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.request.SheetUploadForm;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SheetServiceImpl implements SheetService {

    private final FileUploadUtil fileUploadUtil;
    private final SheetRepository sheetRepository;
    private final SongRepository songRepository;
    private final AuthService authService;
    private final MusicService musicService;

    @Override
    @Transactional
    public Long registerSheetAndMidFileAndSplit(SheetUploadForm sheetUploadForm)
            throws IllegalArgumentException {
        String uuid = UUID.randomUUID().toString();
        fileUploadUtil.uploadSheet(sheetUploadForm.getFile(), uuid);
        Sheet sheet = registerSheet(sheetUploadForm, uuid);
        musicService.saveMidFileWithSplit(sheet.getFileName());
        return sheet.getId();
    }

    @Override
    public Sheet searchById(Long sheetId) throws IllegalArgumentException {
        return sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 악보입니다."));
    }

    @Transactional
    @Override
    public SheetDetailDto searchSheetDetailById(Long sheetId) throws IllegalArgumentException {
        sheetRepository.updateViewCount(sheetId);

        SheetDetailDto sheet = sheetRepository.findSheetDetailViewDtoById(sheetId,
                authService.getLoginUser());
        sheet.loadSongImg(fileUploadUtil);
        return sheet;

    }

    @Override
    public Resource getSheetFileByFileName(String fileName) throws IllegalArgumentException {
        return fileUploadUtil.downloadSheetFile(fileName);
    }

    @Override
    public List<SheetDetailDto> searchSheetByFilter(SheetSearchFilter sheetSearchFilter)
            throws IllegalArgumentException {
        User loginUser = authService.getLoginUser();
        if (loginUser == null) {
            return sheetRepository.findSheetsByFilter(sheetSearchFilter)
                    .stream()
                    .peek(dto -> dto.loadSongImg(fileUploadUtil))
                    .toList();
        } else {
            return sheetRepository.findSheetsByFilter(sheetSearchFilter, loginUser)
                    .stream()
                    .peek(dto -> dto.loadSongImg(fileUploadUtil))
                    .toList();
        }
//        throw new IllegalArgumentException("잘못된 접근입니다");
    }

    @Override
    public String getMusicXmlFileById(Long sheetId) {
        SheetDetailDto sheet = sheetRepository.findSheetDetailViewDtoById(sheetId);
        return fileUploadUtil.getMusicXml(sheet.getFileName());
    }

    @Override
    public String getMidFileById(Long sheetId) {
        SheetDetailDto sheet = sheetRepository.findSheetDetailViewDtoById(sheetId);
        return fileUploadUtil.getMid(sheet.getFileName());
    }

    @Override
    @Transactional
    public void changeStatusToWaiting(Long sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId).orElseThrow();
        sheet.updateStatus(0);
        sheetRepository.save(sheet);
    }

    @Override
    @Transactional
    public void changeStatusToApproved(Long sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId).orElseThrow();
        sheet.updateStatus(1);
        sheetRepository.save(sheet);
    }

    @Override
    @Transactional
    public void changeStatusToRejected(Long sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId).orElseThrow();
        sheet.updateStatus(2);
        sheetRepository.save(sheet);
    }

    @Override
    @Transactional
    public boolean deleteSheet(Long sheetId) {
        User loginUser = authService.getLoginUser();
        Sheet sheet = sheetRepository.findById(sheetId).orElseThrow();
        if (loginUser == null) {
            return false;
        }
        if (!loginUser.getRole().equals("ROLE_ADMIN")
                && loginUser != sheet.getUploader()) {
            return false;
        }
        sheetRepository.deleteById(sheetId);
        return true;
    }


    private Sheet registerSheet(SheetUploadForm sheetUploadForm, String fileName)
            throws IllegalArgumentException {
        try {
            Sheet sheet = Sheet.builder()
                    .uploader(authService.getLoginUser())
                    .level(sheetUploadForm.getLevel())
                    .title(sheetUploadForm.getTitle())
                    .song(sheetUploadForm.getSongId() != null ? songRepository.findById(
                            sheetUploadForm.getSongId()).orElse(null) : null)
                    .fileName(fileName)
                    .build();
            return sheetRepository.save(sheet);
        } catch (Exception e) {
            throw new IllegalArgumentException("악보 저장 실패");
        }
    }

}