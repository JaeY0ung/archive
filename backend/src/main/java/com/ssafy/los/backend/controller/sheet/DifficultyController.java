package com.ssafy.los.backend.controller.sheet;

import com.ssafy.los.backend.dto.sheet.request.DifficultyCreateDto;
import com.ssafy.los.backend.dto.sheet.request.DifficultyUpdateDto;
import com.ssafy.los.backend.dto.sheet.response.DifficultyResponseDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.sheet.DifficultyService;
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
    private final AuthService authService;


    // 난이도 평가 생성
    @PostMapping("/{sheet-id}/difficulties")
    public ResponseEntity<?> crateDifficulty(@PathVariable("sheet-id") Long sheetId,
            @RequestBody DifficultyCreateDto difficultyCreateDto) {
        Long userId = authService.getLoginUser().getId();
        Long saveId = difficultyRatingService.saveDifficulty(sheetId, userId,
                difficultyCreateDto);
        return new ResponseEntity<>(saveId, HttpStatus.OK);
    }

    // 난이도 평가 수정
    @PutMapping("/difficulties/{difficulty-id}")
    public ResponseEntity<?> updateDifficulty(@PathVariable("difficulty-id") Long difficultyId,
            @RequestBody DifficultyUpdateDto difficultyUpdateDto) {
        Long updateId = difficultyRatingService.updateDifficulty(difficultyId,
                difficultyUpdateDto);

        return new ResponseEntity<>(updateId, HttpStatus.OK);
    }

    // 난이도 평가 삭제
    @DeleteMapping("/difficulties/{difficulty-id}")
    public ResponseEntity<?> deleteDifficulty(@PathVariable("difficulty-id") Long difficultyId) {
        difficultyRatingService.deleteDifficulty(difficultyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 악보 난이도 평가 목록 조회
    @GetMapping("/{sheet-id}/difficulties")
    public ResponseEntity<?> findDifficulty(@PathVariable("sheet-id") Long sheetId) {
        List<DifficultyResponseDto> difficultyResponseDto = difficultyRatingService.searchDifficultyBySheetId(
                sheetId);
        return new ResponseEntity<>(difficultyResponseDto, HttpStatus.OK);
    }
}