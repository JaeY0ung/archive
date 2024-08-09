package com.ssafy.los.backend.controller.sheet;

import com.ssafy.los.backend.domain.entity.Song;
import com.ssafy.los.backend.dto.sheet.request.SheetSearchFilter;
import com.ssafy.los.backend.dto.sheet.request.SheetUploadForm;
import com.ssafy.los.backend.dto.sheet.response.SheetDetailDto;
import com.ssafy.los.backend.dto.song.request.SongRegisterForm;
import com.ssafy.los.backend.service.sheet.SheetService;
import com.ssafy.los.backend.service.song.SongService;
import java.nio.charset.StandardCharsets;
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

    private final SongService songService;

//    @Value("${mid-file.path}")
//    private String filePath;

    @PostMapping(value = "/insert/all", consumes = {"multipart/form-data"})
    public void uploadSheet(@RequestPart(value = "files", required = false) List<MultipartFile>
            files) {

        if (files == null || files.isEmpty()) {
            log.info("파일이 포함되어 있지 않습니다.");
            return;
        }
//        File folder = new File(filePath);
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                log.info("파일이 비어 있습니다.");
            }

            Song song = songService.registerSongAndFile(
                    SongRegisterForm.builder()
                            .composer(file.getOriginalFilename())
                            .title(file.getOriginalFilename())
                            .genreId(5L)
                            .file(null)
                            .build()
            );
            uploadSheet(file, file.getOriginalFilename(), 1, song.getId());
        }
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
