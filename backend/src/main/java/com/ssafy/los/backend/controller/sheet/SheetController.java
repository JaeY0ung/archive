package com.ssafy.los.backend.controller.sheet;

import com.ssafy.los.backend.domain.entity.Song;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.request.SheetUploadForm;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailForUserDto;
import com.ssafy.los.backend.dto.song.request.SongRegisterForm;
import com.ssafy.los.backend.service.sheet.SheetService;
import com.ssafy.los.backend.service.song.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/sheets")
public class SheetController {

    private final SongService songService;

    @PostMapping(value = "/insert/all", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadSheet(
            @RequestPart(value = "files", required = false) List<MultipartFile>
                    files) {

        if (files == null || files.isEmpty()) {
            log.debug("파일이 포함되어 있지 않습니다.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        for (MultipartFile file : files) {
            try {
                if (file.isEmpty()) {
                    log.info("파일이 비어 있습니다.");
                    continue;
                }

                Song song = songService.registerSongAndFile(
                        SongRegisterForm.builder()
                                .composer(file.getOriginalFilename())
                                .title(file.getOriginalFilename())
                                .genreId(5L)
                                .file(null)
                                .build()
                );

                SheetUploadForm sheetUploadForm = SheetUploadForm.builder()
                        .file(file)
                        .title(file.getOriginalFilename())
                        .level(1)
                        .songId(song.getId())
                        .build();

                sheetService.registerSheetAndMidFileAndSplit(sheetUploadForm);
//            uploadSheet(file, file.getOriginalFilename(), 1, song.getId());
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    private final SheetService sheetService;

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
                    sheetService.registerSheetAndMidFileAndSplit(sheetUploadForm),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("파일 업로드에 실패했습니다" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getSheetListByFilter(
            @ModelAttribute SheetSearchFilter sheetSearchFilter) {
        return new ResponseEntity<>(sheetService.searchSheetByFilter(sheetSearchFilter),
                HttpStatus.OK);
    }

    @GetMapping("/{sheet-id}")
    public ResponseEntity<?> getSheetInfo(@PathVariable("sheet-id") Long sheetId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(sheetService.searchSheetDetailById(sheetId));
    }

    @DeleteMapping("/{sheet-id}")
    public ResponseEntity<?> deleteSheet(@PathVariable("sheet-id") Long sheetId) {
        if (!sheetService.deleteSheet(sheetId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{sheet-id}/status/0")
    public ResponseEntity<?> changeSheetStatusToWaiting(@PathVariable("sheet-id") Long sheetId) {
        sheetService.changeStatusToWaiting(sheetId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{sheet-id}/status/1")
    public ResponseEntity<?> changeSheetStatusToApproved(@PathVariable("sheet-id") Long sheetId) {
        sheetService.changeStatusToApproved(sheetId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{sheet-id}/status/2")
    public ResponseEntity<?> changeSheetStatusToRejected(@PathVariable("sheet-id") Long sheetId) {
        sheetService.changeStatusToRejected(sheetId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{sheet-id}/download")
    public ResponseEntity<?> downloadSheetMidFile(@PathVariable("sheet-id") Long sheetId) {
        // TODO : 구매여부 확인
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //    @GetMapping("/profile/like/{user-id}")
    //    public ResponseEntity<?> getUserProfileLikedSheet(@PathVariable("user-id") Long userId) {
    //        List<SheetDetailForUserDto> sheetList = sheetService.searchSheetByUserLike(userId);
    //        return new ResponseEntity<List<SheetDetailForUserDto>>(sheetList, HttpStatus.OK);
    //    }

}
