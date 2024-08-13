package com.ssafy.los.backend.service.sheet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.play.SinglePlayResultRepository;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.domain.repository.song.SongRepository;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.request.SheetUpdateFormDto;
import com.ssafy.los.backend.dto.sheet.request.SheetUploadForm;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailForUserDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SheetServiceImpl implements SheetService {

    @Value("${cors.allowedOrigins.music-engine}")
    private String musicEngineBaseUrl;

    private final FileUploadUtil fileUploadUtil;
    private final SheetRepository sheetRepository;
    private final SongRepository songRepository;
    private final AuthService authService;
    private final MusicService musicService;
    private final SinglePlayResultRepository singlePlayResultRepository;
    private final DifficultyService difficultyService;

    @Override
    @Transactional
    public Long registerSheet(SheetUploadForm sheetUploadForm)
            throws IllegalArgumentException {
        Sheet sheet = registerSheetAndMidFileAndSplit(sheetUploadForm);
        return sheet.getId();
    }

    @Override
    @Transactional
    public Long registerSheetWithPredictLevel(SheetUploadForm sheetUploadForm)
            throws IllegalArgumentException {
        Sheet sheet = registerSheetAndMidFileAndSplit(sheetUploadForm);
        //difficultyService.predictLevel(sheet);
        return sheet.getId();
    }

    @Override
    public void updateSheetLevel(Long sheetId) {
        try {
            difficultyService.predictLevelBySheetId(sheetId);
        } catch (Exception e) {
            throw new IllegalArgumentException("난이도 예측 요청이 실패하였습니다.", e);
        }
    }

    private Sheet registerSheetAndMidFileAndSplit(SheetUploadForm sheetUploadForm)
            throws IllegalArgumentException {
        String uuid = UUID.randomUUID().toString();
        fileUploadUtil.uploadSheet(sheetUploadForm.getFile(), uuid);
        Sheet sheet = registerSheet(sheetUploadForm, uuid);
        musicService.saveMidFileWithSplit(sheet.getUuid() + ".mid");
        return sheet;
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
    public Resource getSheetFileByFileName(String uuid) throws IllegalArgumentException {
        return fileUploadUtil.downloadSheetFile(uuid);
    }

    @Override
    public List<SheetDetailDto> searchSheetByFilter(SheetSearchFilter sheetSearchFilter)
            throws IllegalArgumentException {
        return sheetRepository.findSheetsByFilterAndLoginUser(sheetSearchFilter,
                        authService.getLoginUser())
                .stream()
                .peek(dto -> dto.loadSongImg(fileUploadUtil))
                .toList();
    }

    @Override
    public String getMusicXmlFileById(Long sheetId) {
        SheetDetailDto sheet = sheetRepository.findSheetDetailViewDtoById(sheetId,
                authService.getLoginUser());
        return fileUploadUtil.getMusicXmlByUuid(sheet.getUuid());
    }

    @Override
    public String getMidFileById(Long sheetId) {
        SheetDetailDto sheet = sheetRepository.findSheetDetailViewDtoById(sheetId,
                authService.getLoginUser());
        return fileUploadUtil.getMidByUuid(sheet.getUuid());
    }

    @Override
    @Transactional
    public void changeStatusByStatus(Long sheetId, Integer status) {
        Sheet sheet = sheetRepository.findById(sheetId).orElseThrow();
        if (status.equals(0)) {
            changeStatusToWaiting(sheet);
        } else if (status.equals(1)) {
            changeStatusToApproved(sheet);
        } else if (status.equals(2)) {
            changeStatusToRejected(sheet);
        }
    }

    // 최근에 플레이한 싱글 플레이를 가져온다.
    @Override
    public Sheet searchSheetPlayLatest() {
        User loginUser = authService.getLoginUser();
        if (loginUser == null) {
            return null;
        }
//        SinglePlayResult singleLatestResult = singlePlayResultRepository.findByUserOrderByCreatedAtDesc(
//                loginUser).orElse(null);
        SinglePlayResult singleLatestResult = singlePlayResultRepository.findOrderCreatedAtByUser(
                loginUser);

        if (singleLatestResult == null) {
            return null;
        }
        return singleLatestResult.getSheet();
    }

    @Override
    public List<SheetDetailForUserDto> searchSheetDetailByFileName(List<String> fileNames) {
        return sheetRepository.searchByFileName(fileNames);
    }

    @Override
    public List<SheetDetailForUserDto> getRecommendedSheets() {
        Sheet sheet = searchSheetPlayLatest();
        if (sheet == null) {
            log.info("sheet가 비어 있습니다.");
            return Collections.emptyList();
        }
        log.info("가장 최근에 플레이한 악보의 제목: {}", sheet.getTitle());

        // Python 서버에 파일 이름을 보내고 JSON 응답을 받음
        String response = musicService.searchRecommendMidFile(
                musicEngineBaseUrl + "/process-midi", sheet.getUuid() + ".mid");
//            String response = musicService.searchRecommendMidFile(
//                    musicEngineBaseUrl + "/process-midi",
//                    "아로하.mid");
        log.info(response);
        // JSON 응답을 파싱하여 file_name 값들을 추출
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JsonNode similarSongsNode = rootNode.get("similar_songs");

        List<String> uuids = new ArrayList<>();
        for (JsonNode songNode : similarSongsNode) {
            String uuid = songNode.get("uuid").asText();
            uuids.add(uuid);
        }

        // 추출한 file_name을 기반으로 Sheet 정보를 조회하고, SheetDto로 변환
        return searchSheetDetailByFileName(uuids);
    }

    @Override
    public List<SheetDetailDto> searchAllSheetsByStatusForAdmin(SheetSearchFilter sheetSearchFilter)
            throws IllegalArgumentException {
        User loginUser = authService.getLoginUser();
        if (loginUser == null || !loginUser.getRole().equals("ROLE_ADMIN")) {
            throw new IllegalArgumentException();
        }
        return sheetRepository.findSheetsByStatusForAdmin(sheetSearchFilter)
                .stream()
                .peek(dto -> dto.loadSongImg(fileUploadUtil))
                .toList();
    }

    @Override
    public Sheet updateSheet(Long sheetId, SheetUpdateFormDto sheetUpdateFormDto) {
        Sheet sheet = sheetRepository.findById(sheetId).orElseThrow();
        sheet.updateTitleAndLevel(sheetUpdateFormDto.getTitle(), sheetUpdateFormDto.getLevel());
        return sheetRepository.save(sheet);
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

    private Sheet registerSheet(SheetUploadForm sheetUploadForm, String uuid)
            throws IllegalArgumentException {
        try {
            Sheet sheet = Sheet.builder()
                    .uploader(authService.getLoginUser())
                    .level(sheetUploadForm.getLevel())
                    .title(sheetUploadForm.getTitle())
                    .song(sheetUploadForm.getSongId() != null ? songRepository.findById(
                            sheetUploadForm.getSongId()).orElse(null) : null)
                    .uuid(uuid)
                    .build();
            return sheetRepository.save(sheet);
        } catch (Exception e) {
            throw new IllegalArgumentException("악보 저장 실패");
        }
    }

    @Override
    public List<SheetDetailForUserDto> searchSheetByUserLike(Long userId) {
        return sheetRepository.searchByUserLike(userId).stream()
                .peek(sheet -> sheet.loadSongImg(fileUploadUtil))
                .collect(Collectors.toList());
    }

    @Override
    public SheetDetailDto searchRecentSinglePlayedSheet() {
        User loginUser = authService.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        SheetDetailDto sheet = sheetRepository.searchOneRecentSinglePlayedSheet(loginUser);
        sheet.loadSongImg(fileUploadUtil);
        return sheet;
    }


    private void changeStatusToWaiting(Sheet sheet) {
        sheet.updateStatus(0);
        sheetRepository.save(sheet);
    }

    private void changeStatusToApproved(Sheet sheet) {
        sheet.updateStatus(1);
        sheetRepository.save(sheet);
    }

    private void changeStatusToRejected(Sheet sheet) {
        sheet.updateStatus(2);
        sheetRepository.save(sheet);
    }
}