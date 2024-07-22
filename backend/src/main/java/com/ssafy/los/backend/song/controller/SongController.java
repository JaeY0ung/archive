package com.ssafy.los.backend.song.controller;

import com.ssafy.los.backend.song.model.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @GetMapping
    public ResponseEntity<?> getSongsByFilter(@RequestParam(defaultValue = "") String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new ResponseEntity<>(songService.searchAll(), HttpStatus.OK);
        }
        return new ResponseEntity<>(songService.searchSongByTitle(keyword),
                HttpStatus.OK);
    }

    @GetMapping("/{song-id}")
    public ResponseEntity<?> getSongById(@PathVariable("song-id") Long songId) {
        return new ResponseEntity<>(songService.searchById(songId), HttpStatus.OK);
    }
}
