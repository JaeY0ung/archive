package com.ssafy.los.backend.controller.sheet;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.request.SheetUploadForm;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailViewDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.sheet.SheetService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    // 악보 관리
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
            "multipart/form-data"})
    public ResponseEntity<?> uploadSheet(
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart("title") String title,
            @RequestPart("level") Integer level,
            @RequestPart("songId") Long songId) {

        if (files.size() != 1) {
            return new ResponseEntity<>("하나의 파일만 올려주세요.", HttpStatus.BAD_REQUEST);
        }

        SheetUploadForm sheetUploadForm = SheetUploadForm.builder()
                .file(files.get(0))
                .title(title)
                .level(level)
                .songId(songId)
                .build();
        try { // 악보 데이터 및 파일 저장
            Sheet sheet = sheetService.registerSheetAndFile(sheetUploadForm);
            return new ResponseEntity<>(sheet, HttpStatus.CREATED);
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
                .body(sheetService.searchSheetById(sheetId));
    }

    @GetMapping("/{sheet-id}/download")
    public ResponseEntity<?> downloadSheet(@PathVariable("sheet-id") Long sheetId) {
        // TODO : 구매여부 확인
        SheetDetailViewDto sheet = sheetService.searchSheetById(sheetId);
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
}
