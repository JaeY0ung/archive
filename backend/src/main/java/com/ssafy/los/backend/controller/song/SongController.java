package com.ssafy.los.backend.controller.song;

import com.ssafy.los.backend.dto.song.request.SongRegisterForm;
import com.ssafy.los.backend.service.song.SongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
@Slf4j
public class SongController {

    private final SongService songService;

    @GetMapping
    public ResponseEntity<?> getSongsByFilter(@RequestParam(defaultValue = "") String keyword) {
        return new ResponseEntity<>(songService.searchSongByFilter(keyword),
                HttpStatus.OK);
    }

    @GetMapping("/{song-id}")
    public ResponseEntity<?> getSongById(@PathVariable("song-id") Long songId) {
        return new ResponseEntity<>(songService.searchById(songId), HttpStatus.OK);
    }

    @DeleteMapping("/{song-id}")
    public ResponseEntity<?> deleteSongById(@PathVariable("song-id") Long songId) {
        if (!songService.deleteById(songId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
            "multipart/form-data"})
    public ResponseEntity<?> uploadSong(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("title") String title,
            @RequestPart("title") String composer,
            @RequestPart(value = "genreId", required = false) Long genreId) {
        SongRegisterForm songRegisterForm = SongRegisterForm.builder()
                .file(file)
                .title(title)
                .composer(composer)
                .genreId(genreId)
                .build();
        log.info(songRegisterForm.toString());
        try {
            return new ResponseEntity<>(songService.registerSongAndFile(songRegisterForm).getId(),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("파일 업로드에 실패했습니다." + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
