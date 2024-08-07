package com.ssafy.los.backend.controller.play;

import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.dto.play.request.SinglePlayRequestDto;
import com.ssafy.los.backend.service.play.SinglePlayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plays/single")
public class SinglePlayController {

    private final SinglePlayService singlePlayService;

//    // 싱글 중간 결과 생성
//    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, "multipart/form-data"})
//    public ResponseEntity<SinglePlayResult> sendIntermediateScoreToPython(@RequestBody SinglePlayRequestDto dto) {
//
//        return ResponseEntity<>
//    }

    // 싱글 최종 결과 생성
    @PostMapping
    private ResponseEntity<?> saveSinglePlayResult(SinglePlayRequestDto singlePlayRequestDto) {
        Long singePlayResultId = singlePlayService.saveSinglePlayResult(singlePlayRequestDto);

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

    @DeleteMapping("/single-result-id")
    private ResponseEntity<?> deleteSinglePlayResult(
            @PathVariable("single-result-id") Long singleResultId) {
        Long deletedSingleResultId = singlePlayService.removeSinglePlayResult(singleResultId);
        return new ResponseEntity<>(deletedSingleResultId, HttpStatus.OK);
    }


}
