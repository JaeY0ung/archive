package com.ssafy.los.backend.controller.sheet;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.request.SheetUploadForm;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.sheet.MusicService;
import com.ssafy.los.backend.service.sheet.SheetService;
import java.nio.charset.StandardCharsets;
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
    private final AuthService authService;
    private final MusicService musicService;
    private final SheetRepository sheetRepository;

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
            Sheet sheet = sheetService.registerSheetAndFile(sheetUploadForm);
            musicService.saveMidFileWithSplit(sheet.getFileName());
            return new ResponseEntity<>(sheet.getId(), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("파일 업로드에 실패했습니다." + e.getMessage(), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> downloadSheet(@PathVariable("sheet-id") Long sheetId) {
        // TODO : 구매여부 확인
        SheetDetailDto sheet = sheetService.searchSheetDetailById(sheetId);
        try {
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            String.format("attachment; filename=\"%s.%s\"",
                                    UriUtils.encode(sheet.getTitle(),
                                            StandardCharsets.UTF_8),
                                    FilenameUtils.getExtension(sheet.getFileName()))
                    )
                    .body(sheetService.getSheetFileByFileName(sheet.getFileName()));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("다운로드에 실패했습니다", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{sheet-id}/music-xml")
    public ResponseEntity<?> getSheetMusicXmlFileBySheetId(@PathVariable("sheet-id") Long sheetId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(sheetService.getMusicXmlFileById(sheetId));
    }

    @GetMapping("/{sheet-id}/mid")
    public ResponseEntity<?> getSheetMidFileBySheetId(@PathVariable("sheet-id") Long sheetId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(sheetService.getMidFileById(sheetId));
    }
}
