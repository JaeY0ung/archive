package com.ssafy.los.backend.sheet.controller;

import com.ssafy.los.backend.sheet.model.dto.request.DifficultyRatingRequest;
import com.ssafy.los.backend.sheet.model.service.DifficultyRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/difficulty-rating")
public class DifficultyRatingConroller {

    private final DifficultyRatingService difficultyRatingService;


    // 난이도 생성
    @PostMapping("")
    public ResponseEntity<?> saveDifficultyRating(@RequestBody DifficultyRatingRequest request) {
        Long findId = difficultyRatingService.saveDifficultyRating(request);
        return new ResponseEntity<>(findId, HttpStatus.OK);
    }

    // 난이도 수정
    @GetMapping("/{id}")
    public ResponseEntity<?> updateDifficultyRating(@RequestBody DifficultyRatingRequest request,
            @PathVariable Long id) {
        Long updateId = difficultyRatingService.updateDifficultyRating(id, request);

        return new ResponseEntity<>(updateId, HttpStatus.OK);
    }

    // 난이도 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDifficultyRating(@PathVariable Long id) {
        difficultyRatingService.deleteStarRate(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 난이도 가져오기
    @GetMapping("/{sheetId}")
    public ResponseEntity<?> findDifficultyRating(@PathVariable Long sheetId) {
        int calculatedLevel = difficultyRatingService.findDifficultyRating(sheetId);
        return new ResponseEntity<>(calculatedLevel, HttpStatus.OK);
    }
}
