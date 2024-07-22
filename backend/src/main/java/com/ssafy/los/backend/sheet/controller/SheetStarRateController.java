package com.ssafy.los.backend.sheet.controller;

import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateRequest;
import com.ssafy.los.backend.sheet.model.entity.SheetStarRate;
import com.ssafy.los.backend.sheet.model.service.SheetStarRateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sheet-star-rates")
public class SheetStarRateController {

    private final SheetStarRateService sheetStarRateService;


    // 리뷰 생성하기
    @PostMapping
    public ResponseEntity<?> saveSheetStarRate(@RequestBody SheetStarRateRequest request) {
        Long findId = sheetStarRateService.saveStarRate(request);

        return new ResponseEntity<>(findId, HttpStatus.OK);
    }

    // 리뷰 수정하기 -> @PathVariable에 ("id") 우선 넣기
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStarRate(@RequestBody SheetStarRateRequest request,
            @PathVariable Long id) {
        Long updateId = sheetStarRateService.updateStarRate(id, request);
        return new ResponseEntity<>(updateId, HttpStatus.OK);
    }

    // 리뷰 삭제하기
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStarRate(@PathVariable Long id) {
        sheetStarRateService.deleteStarRate(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 리뷰 하나 가져오기
    @GetMapping("/{id}")
    public ResponseEntity<?> findStarRate(@PathVariable Long id) {
        SheetStarRate sheetStarRate = sheetStarRateService.findStarRateById(id);
        return new ResponseEntity<>(sheetStarRate, HttpStatus.OK);
    }

    // 특정 악보 리뷰 조회하기
    @GetMapping("/star-rate/{sheet-id}") // 수정 필요
    public ResponseEntity<?> findStarRateBySheetId(@PathVariable("sheet-id") Long sheetId) {
        List<SheetStarRate> sheetStarRates = sheetStarRateService.findStarRateBySheetId(sheetId);
        return new ResponseEntity<>(sheetStarRates, HttpStatus.OK);

    }

}
