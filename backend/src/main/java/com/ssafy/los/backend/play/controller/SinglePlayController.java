package com.ssafy.los.backend.play.controller;

import com.ssafy.los.backend.play.model.dto.request.SinglePlayRequestDto;
import com.ssafy.los.backend.play.model.entity.SinglePlayResult;
import com.ssafy.los.backend.play.model.service.SinglePlayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plays/single")
public class SinglePlayController {

    private final SinglePlayService singlePlayService;

    // 싱글결과 생성
    @PostMapping
    private ResponseEntity<?> saveSinglePlayResult(SinglePlayRequestDto singlePlayRequestDto) {
        Long singePlayResultId = singlePlayService.saveSinglePlayResult(singlePlayRequestDto);

        return new ResponseEntity<>(singePlayResultId, HttpStatus.CREATED);
    }

    // 생성날짜를 기준으로 싱글 결과를 조회한다.
    @GetMapping
    private ResponseEntity<?> getSinglePlayResult(@PageableDefault(size=12, sort = "createdAt") Pageable pageable) {
        Page<SinglePlayResult> singlePlayResultList = singlePlayService.getSinglePlayResultList(
                pageable);

        return new ResponseEntity<>(singlePlayResultList, HttpStatus.OK);
    }

    @DeleteMapping("/single-result-id")
    private ResponseEntity<?> deleteSinglePlayResult(@PathVariable("single-result-id") Long singleResultId) {
        Long deletedSingleResultId = singlePlayService.removeSinglePlayResult(singleResultId);
        return new ResponseEntity<>(deletedSingleResultId, HttpStatus.OK);
    }






}
