package com.ssafy.los.backend.controller.sheet;

import com.ssafy.los.backend.domain.entity.Song;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.request.SheetUpdateFormDto;
import com.ssafy.los.backend.dto.sheet.request.SheetUploadForm;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailForUserDto;
import com.ssafy.los.backend.dto.song.request.SongRegisterForm;
import com.ssafy.los.backend.service.sheet.SheetService;
import com.ssafy.los.backend.service.song.SongService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/sheets")
public class SheetController {

    private final SheetService sheetService;
    private final SongService songService;

    @GetMapping
    public ResponseEntity<?> getSheetListByFilter(
            @ModelAttribute SheetSearchFilter sheetSearchFilter) {
        try {
            return new ResponseEntity<>(sheetService.searchSheetByFilter(sheetSearchFilter),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{sheet-id}")
    public ResponseEntity<?> getSheetById(@PathVariable("sheet-id") Long sheetId) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(sheetService.searchSheetDetailById(sheetId));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/recent-play")
    public ResponseEntity<?> getRecentSinglePlayedSheet() {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(sheetService.searchRecentSinglePlayedSheet());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
            "multipart/form-data"})
    public ResponseEntity<?> uploadSheet(
            @RequestPart(value = "files", required = false) MultipartFile file,
            @RequestPart("title") String title,
            @RequestPart("level") Integer level,
            @RequestPart(value = "songId", required = false) Long songId) {
        SheetUploadForm sheetUploadForm = SheetUploadForm.builder()
                .file(file)
                .title(title)
                .level(level)
                .songId(songId)
                .build();
        try { // 악보 데이터 및 파일 저장
            return new ResponseEntity<>(
                    sheetService.registerSheetWithPredictLevel(sheetUploadForm),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("[파일 업로드 실패] " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/insert/all", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadSheet(
            @RequestPart(value = "files", required = false) List<MultipartFile>
                    files, @RequestPart("level") Integer level) {

        if (files == null || files.isEmpty()) {
            log.debug("파일이 포함되어 있지 않습니다.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        for (MultipartFile file : files) {
            try {
                if (file == null || file.isEmpty()) {
                    continue;
                }

                String fileName = file.getOriginalFilename();
                if (fileName == null || fileName.isEmpty()) {
                    continue;
                }
                String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

                Song song = songService.registerSongAndFile(
                        SongRegisterForm.builder()
                                .composer(baseName)
                                .title(baseName)
                                .genreId(5L)
                                .file(null)
                                .build()
                );

                SheetUploadForm sheetUploadForm = SheetUploadForm.builder()
                        .file(file)
                        .title(baseName)
                        .level(level)
                        .songId(song.getId())
                        .build();

                sheetService.registerSheet(sheetUploadForm);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
        return new ResponseEntity<>("[업로드 완료]", HttpStatus.OK);
    }

    @PutMapping("/{sheet-id}")
    public ResponseEntity<?> updateSheet(@PathVariable("sheet-id") Long sheetId,
            @RequestBody SheetUpdateFormDto sheetUpdateFormDto) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(sheetService.updateSheet(sheetId, sheetUpdateFormDto));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/{sheet-id}")
    public ResponseEntity<?> deleteSheet(@PathVariable("sheet-id") Long sheetId) {
        try {
            if (!sheetService.deleteSheet(sheetId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{sheet-id}/status/{status}")
    public ResponseEntity<?> changeSheetStatusToWaiting(@PathVariable("status") Integer status,
            @PathVariable("sheet-id") Long sheetId) {
        if (!new ArrayList<>(Arrays.asList(0, 1, 2)).contains(status)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        sheetService.changeStatusByStatus(sheetId, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getSheetListByStatusForAdmin(
            @ModelAttribute SheetSearchFilter sheetSearchFilter) {
        if (!checkRightStatuses(sheetSearchFilter.getStatuses())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(sheetService.searchAllSheetsByStatusForAdmin(sheetSearchFilter),
                HttpStatus.OK);
    }

    private boolean checkRightStatuses(Integer[] statuses) {
        for (Integer status : statuses) {
            if (!status.equals(0) && !status.equals(1) && !status.equals(2)) {
                return false;
            }
        }
        return true;
    }

    @GetMapping("/{sheet-id}/download")
    public ResponseEntity<?> downloadSheetMidFile(@PathVariable("sheet-id") Long sheetId) {
        // TODO : 구매 여부 확인
        SheetDetailDto sheet = sheetService.searchSheetDetailById(sheetId);
        try {
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            String.format("attachment; filename=\"%s.%s\"",
                                    UriUtils.encode(sheet.getTitle(),
                                            StandardCharsets.UTF_8),
                                    FilenameUtils.getExtension(sheet.getUuid()))
                    )
                    .body(sheetService.getSheetFileByFileName(sheet.getUuid()));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("다운로드에 실패했습니다", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{sheet-id}/music-xml")
    public ResponseEntity<?> getSheetMusicXmlFileBySheetId(@PathVariable("sheet-id") Long sheetId) {
        String musicXmlFile = sheetService.getMusicXmlFileById(sheetId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(musicXmlFile);
    }

    @GetMapping("/{sheet-id}/mid")
    public ResponseEntity<?> getSheetMidFileBySheetId(@PathVariable("sheet-id") Long sheetId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(sheetService.getMidFileById(sheetId));
    }

    // 추천 악보를 보내준다.
    @GetMapping("/recommend")
    public ResponseEntity<?> getRecommendSheet() {
        try {
            List<SheetDetailForUserDto> recommendedSheets = sheetService.getRecommendedSheets();
            return new ResponseEntity<>(recommendedSheets, HttpStatus.OK);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/like/{user-id}")
    public ResponseEntity<?> getUserProfileLikedSheet(@PathVariable("user-id") Long userId) {
        List<SheetDetailForUserDto> sheetList = sheetService.searchSheetByUserLike(userId);
        return new ResponseEntity<>(sheetList, HttpStatus.OK);
    }

    @PostMapping("/predict-level/{sheet-id}")
    public ResponseEntity<?> updateSheetLevel(@PathVariable("sheet-id") Long sheetId) {
        log.info("난이도 예측 요청이 들어왔습니다.");
        sheetService.updateSheetLevel(sheetId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
