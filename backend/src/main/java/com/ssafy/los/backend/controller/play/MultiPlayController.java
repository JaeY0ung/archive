package com.ssafy.los.backend.controller.play;

import com.ssafy.los.backend.dto.play.request.MultiPlayResultAfterDto;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultBeforeDto;
import com.ssafy.los.backend.dto.play.response.MultiPlayResultProfileDto;
import com.ssafy.los.backend.service.play.MultiPlayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/plays/multi")
public class MultiPlayController {

    private final MultiPlayService multiPlayService;

    // 멀티 중간 결과 생성 후 파이썬 전송
    @PostMapping(value = "/{multi-result-id}/live-score")
    public ResponseEntity<?> sendIntermediateScoreToPython(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("sheetId") Long sheetId,
            @PathVariable(name = "multi-result-id") Long multiResultId) {
        log.info("중간 점수 전송 매핑 성공");
        log.info("sheetId: {}", sheetId);
        log.info("multiResultId: {}", multiResultId);
        try {
            return ResponseEntity.ok(multiPlayService.getLiveScore(file, sheetId, multiResultId));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 게임 시작 시, 멀티 결과 생성
    @PostMapping
    public ResponseEntity<?> saveMultiPlayResult(@RequestBody MultiPlayResultBeforeDto multiResultBeforeDto) {
        log.info("멀티 플레이 테이블을 생성합니다. : {}", multiResultBeforeDto.toString());
        Long multiPlayResultId = multiPlayService.saveMultiPlayResult(multiResultBeforeDto);

        return new ResponseEntity<>(multiPlayResultId, HttpStatus.CREATED);
    }

    // 게임 종료 시, 멀티 결과 업데이트
    @PatchMapping("/{multi-result-id}")
    public ResponseEntity<?> completeMultiPlayResult(
            @PathVariable("multi-result-id") Long multiResultId,
            MultiPlayResultAfterDto multiResultAfterDto) {
        Long multiPlayResult = multiPlayService.completeMultiPlayResult(multiResultId,
                multiResultAfterDto);

        return new ResponseEntity<>(multiPlayResult, HttpStatus.OK);
    }

    // 특정 유저에 대해서 멀티 결과 기록들을 조회한다. (프로필 페이지)
    @GetMapping("/{user-id}")
    public ResponseEntity<?> getMultiPlayResultAllByUser(@PathVariable("user-id") Long userId) {
        List<MultiPlayResultProfileDto> multiPlayResultProfileDtoList = multiPlayService.getMultiPlayResultList(
                userId);
        log.info("MultiPlayResultProfileDtoList: {}", multiPlayResultProfileDtoList.toString());
        return new ResponseEntity<>(multiPlayResultProfileDtoList, HttpStatus.OK);
    }

    // 멀티 기록 삭제
    // TODO: ADMIN 만 멀티 결과에 대해 삭제할 수 있다.
    @DeleteMapping("/{multi-result-id}")
    public ResponseEntity<?> deleteMultiPlayResult(
            @PathVariable("multi-result-id") Long multiResultId) {
        Long deletedSingleResultId = multiPlayService.removeMultiPlayResult(multiResultId);

        return new ResponseEntity<>(deletedSingleResultId, HttpStatus.OK);
    }

}
