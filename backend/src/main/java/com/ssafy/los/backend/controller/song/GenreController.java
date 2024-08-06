package com.ssafy.los.backend.controller.song;

import com.ssafy.los.backend.service.sheet.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<?> searchAllGenres() {
        return new ResponseEntity<>(genreService.searchAllGenres(), HttpStatus.OK);
    }
}
