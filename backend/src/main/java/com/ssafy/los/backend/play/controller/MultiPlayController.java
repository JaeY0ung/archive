package com.ssafy.los.backend.play.controller;

import com.ssafy.los.backend.play.model.dto.request.MultiResultAfterDto;
import com.ssafy.los.backend.play.model.dto.request.MultiResultBeforeDto;
import com.ssafy.los.backend.play.model.dto.request.SinglePlayRequestDto;
import com.ssafy.los.backend.play.model.dto.response.MultiPlayResultDto;
import com.ssafy.los.backend.play.model.dto.response.MultiPlayResultListDto;
import com.ssafy.los.backend.play.model.entity.MultiPlayResult;
import com.ssafy.los.backend.play.model.entity.SinglePlayResult;
import com.ssafy.los.backend.play.model.service.MultiPlayService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plays/multi")
public class MultiPlayController {

    private final MultiPlayService multiPlayService;

    // 게임 시작 시, 멀티 결과 생성
    @PostMapping
    public ResponseEntity<?> saveMultiPlayResult(MultiResultBeforeDto multiResultBeforeDto) {
        Long multiPlayResultId = multiPlayService.saveMultiPlayResult(multiResultBeforeDto);

        return new ResponseEntity<>(multiPlayResultId, HttpStatus.CREATED);
    }

    // 게임 종료 시, 멀티 결과 업데이트
    @PatchMapping("/{multi-result-id}")
    public ResponseEntity<?> completeMultiPlayResult(@PathVariable("multi-result-id") Long multiResultId, MultiResultAfterDto multiResultAfterDto) {

        Long multiPlayResult = multiPlayService.completeMultiPlayResult(multiResultId,
                multiResultAfterDto);

        return new ResponseEntity<>(multiPlayResult, HttpStatus.OK);
    }

    // 멀티 결과를 조회한다.
    @GetMapping("/{multi-result-id}")
    public ResponseEntity<?> getMultiPlayResult(@PathVariable("multi-result-id") Long multiResultId) {

        multiPlayService.getMultiPlayResultList(multiResultId);

        return new ResponseEntity<>(singlePlayResultList, HttpStatus.OK);
    }

    @DeleteMapping("/multi-result-id")
    public ResponseEntity<?> deleteSinglePlayResult(@PathVariable("single-result-id") Long singleResultId) {
        Long deletedSingleResultId = MultiPlayService.removeMultiPlayResult(singleResultId);
        return new ResponseEntity<>(deletedSingleResultId, HttpStatus.OK);
    }
}
