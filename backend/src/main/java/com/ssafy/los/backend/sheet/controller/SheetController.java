package com.ssafy.los.backend.sheet.controller;

import com.ssafy.los.backend.sheet.model.dto.request.SheetUploadForm;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.service.SheetService;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.service.AuthService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            @RequestPart("price") Integer price,
            @RequestPart("level") Integer level,
            @RequestPart("point") Integer point,
            @RequestPart("songId") Long songId) {

        User loginUser = authService.getLoginUser();

        if (files.size() != 1) {
            return new ResponseEntity<>("하나의 파일만 올려주세요.", HttpStatus.BAD_REQUEST);
        }

        SheetUploadForm sheetUploadForm = SheetUploadForm.builder()
                .files(files)
                .title(title)
                .price(price)
                .level(level)
                .point(point)
                .songId(songId)
                .build();
        try {
            String uuid = sheetService.saveSheetFile(files.get(0));
            Sheet sheet = sheetService.saveSheetInfo(sheetUploadForm, loginUser, uuid);
            return new ResponseEntity<>(sheet, HttpStatus.CREATED);
            // TODO : mid -> mp3 변환한 파일 추가로 저장하는 로직 구현하기
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getSheetListByFilter(@RequestParam String sort) {
        return new ResponseEntity<>(sheetService.searchSheetListBySort(sort),
                HttpStatus.OK);
    }

    @GetMapping("/{sheet-id}")
    public ResponseEntity<?> getSheetInfo(@PathVariable("sheet-id") Long sheetId) {
        return new ResponseEntity<>(sheetService.searchSheetById(sheetId), HttpStatus.OK);
    }

    @GetMapping("/{sheet-id}/download")
    public ResponseEntity<?> downloadSheet(@PathVariable("sheet-id") Long sheetId) {
        // TODO : 구매여부 확인

        Sheet sheet = sheetService.searchSheetById(sheetId);
        try {
            Resource resource = sheetService.getSheetFileByName(sheet.getFileName());

            String encodedOriginalFileName = UriUtils.encode(sheet.getTitle(),
                    StandardCharsets.UTF_8);

            String fileExtension = FilenameUtils.getExtension(sheet.getFileName());
            String contentDisposition =
                    "attachment; filename=\"" + encodedOriginalFileName + "." + fileExtension
                            + "\"";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(resource);
        } catch (IOException e) {
            log.info(e.getMessage()); // 파일이 없습니다.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
