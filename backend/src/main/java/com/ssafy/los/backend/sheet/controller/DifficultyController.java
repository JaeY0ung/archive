package com.ssafy.los.backend.sheet.controller;

import com.ssafy.los.backend.sheet.model.dto.request.DifficultyCreateRequestDto;
import com.ssafy.los.backend.sheet.model.dto.request.DifficultyUpdateRequestDto;
import com.ssafy.los.backend.sheet.model.dto.response.DifficultyResponseDto;
import com.ssafy.los.backend.sheet.model.service.DifficultyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sheets")
public class DifficultyController {

    private final DifficultyService difficultyRatingService;


    // 난이도 평가 생성
    @PostMapping("/{sheet-id}/difficulties")
    public ResponseEntity<?> saveDifficultyRating(@PathVariable("sheet-id") Long sheetId,
            @RequestBody DifficultyCreateRequestDto difficultyCreateRequestDto) {
        Long saveId = difficultyRatingService.saveDifficultyRating(sheetId, difficultyCreateRequestDto);
        return new ResponseEntity<>(saveId, HttpStatus.OK);
    }

    // 난이도 평가 수정
    @PutMapping("/difficulties/{difficulty-id}")
    public ResponseEntity<?> updateDifficultyRating(@PathVariable("difficulty-id") Long difficultyId,
            @RequestBody DifficultyUpdateRequestDto difficultyUpdateRequestDto) {
        Long updateId = difficultyRatingService.updateDifficultyRating(difficultyId, difficultyUpdateRequestDto);

        return new ResponseEntity<>(updateId, HttpStatus.OK);
    }

    // 난이도 평가 삭제
    @DeleteMapping("/difficulties/{difficulty-id}")
    public ResponseEntity<?> deleteDifficultyRating(@PathVariable("difficulty-id") Long difficultyId) {
        difficultyRatingService.deleteDifficulty(difficultyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 악보 난이도 평가 목록 조회
    @GetMapping("/{sheet-id}/difficulties")
    public ResponseEntity<?> findDifficultyRating(@PathVariable("sheet-id") Long sheetId) {
        List<DifficultyResponseDto> difficultyResponseDto = difficultyRatingService.findDifficultyRating(sheetId);
        return new ResponseEntity<>(difficultyResponseDto, HttpStatus.OK);
    }
}
