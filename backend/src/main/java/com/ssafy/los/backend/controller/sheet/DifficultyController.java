package com.ssafy.los.backend.controller.sheet;

import com.ssafy.los.backend.dto.sheet.request.DifficultyCreateDto;
import com.ssafy.los.backend.dto.sheet.request.DifficultyUpdateDto;
import com.ssafy.los.backend.dto.sheet.response.DifficultyResponseDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.sheet.DifficultyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sheets")
public class DifficultyController {

    private final DifficultyService difficultyService;
    private final AuthService authService;


    // 난이도 평가 생성
    @PostMapping("/{sheet-id}/difficulties")
    public ResponseEntity<?> crateDifficulty(@PathVariable("sheet-id") Long sheetId,
            @RequestBody DifficultyCreateDto difficultyCreateDto) {
        try {
            Long userId = authService.getLoginUser().getId();
            Long saveId = difficultyService.saveDifficulty(sheetId, userId, difficultyCreateDto);
            return new ResponseEntity<>(saveId, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("난이도 평가 저장 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 난이도 평가 수정
    @PutMapping("/difficulties/{difficulty-id}")
    public ResponseEntity<?> updateDifficulty(@PathVariable("difficulty-id") Long difficultyId,
            @RequestBody DifficultyUpdateDto difficultyUpdateDto) {
        Long updateId = difficultyService.updateDifficulty(difficultyId,
                difficultyUpdateDto);

        return new ResponseEntity<>(updateId, HttpStatus.OK);
    }

    // 난이도 평가 삭제
    @DeleteMapping("/difficulties/{difficulty-id}")
    public ResponseEntity<?> deleteDifficulty(@PathVariable("difficulty-id") Long difficultyId) {
        difficultyService.deleteDifficulty(difficultyId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 악보 난이도 평가 목록 조회 (페이징 적용)
    @GetMapping("/{sheet-id}/difficulties")
    public ResponseEntity<?> findDifficulty(@PathVariable("sheet-id") Long sheetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        log.info("page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Page<DifficultyResponseDto> difficultyResponseDto = difficultyService.searchDifficultyBySheetId(sheetId, page, size, sortBy, sortDir);

        return new ResponseEntity<>(difficultyResponseDto, HttpStatus.OK);
    }

    // 악보 난이도 평가 전체 목록 조회
    @GetMapping("/{sheet-id}/difficulties/all")
    public ResponseEntity<?> findAllDifficulty(@PathVariable("sheet-id") Long sheetId) {
        List<DifficultyResponseDto> difficultyResponseDto = difficultyService.searchDifficultyAllBySheetId(sheetId);

        return new ResponseEntity<>(difficultyResponseDto, HttpStatus.OK);
    }

}
