package com.ssafy.los.backend.controller.play;

import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.dto.play.request.SingleResultAfterDto;
import com.ssafy.los.backend.dto.play.request.SingleResultBeforeDto;
import com.ssafy.los.backend.dto.play.response.SingePlayResultProfileDto;
import com.ssafy.los.backend.service.play.SinglePlayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/plays/single")
public class SinglePlayController {

    private final SinglePlayService singlePlayService;

    // 싱글 중간 결과 생성 후 파이썬 전송
    @PostMapping(value = "/{single-result-id}/live-score")
    public ResponseEntity<?> sendIntermediateScoreToPython(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("sheetId") Long sheetId,
            @PathVariable(name = "single-result-id") Long singleResultId) {
        log.info("중간 점수 전송 매핑 성공");
        log.info("sheetId: {}", sheetId);
        log.info("singleResultId: {}", singleResultId);
        try {
            return ResponseEntity.ok(singlePlayService.getLiveScore(file, sheetId, singleResultId));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 게임 시작 시, 싱글 결과 생성
    @PostMapping
    public ResponseEntity<?> saveSinglePlayResult(
            @RequestBody SingleResultBeforeDto singleResultBeforeDto) {
        log.info("싱글 플레이가 시작되었습니다. : {}", singleResultBeforeDto.toString());
        Long multiPlayResultId = singlePlayService.saveSinglePlayResult(singleResultBeforeDto);

        return new ResponseEntity<>(multiPlayResultId, HttpStatus.CREATED);
    }

    // 게임 종료 시, 싱글 결과 업데이트
    @PatchMapping("/{single-result-id}")
    private ResponseEntity<?> completeSinglePlayResult(
            @PathVariable("single-result-id") Long singleResultId,
            @RequestBody SingleResultAfterDto singleResultAfterDto) {
        log.info("싱글 플레이 데이터 저장 : {}", singleResultAfterDto.toString());

        Long singePlayResultId = singlePlayService.completeSinglePlayResult(singleResultId,
                singleResultAfterDto);

        return new ResponseEntity<>(singePlayResultId, HttpStatus.CREATED);
    }

    // 생성날짜를 기준으로 싱글 결과를 조회한다.
    @GetMapping
    private ResponseEntity<?> getSinglePlayResult(
            @PageableDefault(size = 12, sort = "createdAt") Pageable pageable) {
        Page<SinglePlayResult> singlePlayResultList = singlePlayService.getSinglePlayResultList(
                pageable);

        return new ResponseEntity<>(singlePlayResultList, HttpStatus.OK);
    }

    // 특정 유저에 대해서 싱글 결과 기록들을 조회한다. (프로필 페이지)
    @GetMapping("/{user-id}")
    private ResponseEntity<?> getSinglePlayResultAllByUser(@PathVariable("user-id") Long userId) {
        List<SingePlayResultProfileDto> singlePlayResultList = singlePlayService.getSinglePlayResultListByUser(
                userId);

        return new ResponseEntity<>(singlePlayResultList, HttpStatus.OK);
    }

    @DeleteMapping("/{single-result-id}")
    private ResponseEntity<?> deleteSinglePlayResult(
            @PathVariable("single-result-id") Long singleResultId) {
        Long deletedSingleResultId = singlePlayService.removeSinglePlayResult(singleResultId);
        return new ResponseEntity<>(deletedSingleResultId, HttpStatus.OK);
    }


}
