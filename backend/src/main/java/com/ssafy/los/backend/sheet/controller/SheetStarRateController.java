package com.ssafy.los.backend.sheet.controller;

import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateCreateDto;
import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateUpdateDto;
import com.ssafy.los.backend.sheet.model.dto.response.SheetStarRateDto;
import com.ssafy.los.backend.sheet.model.service.SheetStarRateService;
import com.ssafy.los.backend.user.model.service.AuthService;
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
@RequestMapping("/sheets")
public class SheetStarRateController {

    private final SheetStarRateService sheetStarRateService;
    private final AuthService authService;


    // 리뷰 생성하기
    @PostMapping("{sheet-id}/star-rates")
    public ResponseEntity<?> createSheetStarRate(@PathVariable("sheet-id") Long sheetId,
            @RequestBody SheetStarRateCreateDto sheetStarRateCreateDto) {
        Long createId = sheetStarRateService.saveSheetStarRate(sheetStarRateCreateDto, sheetId);
        return new ResponseEntity<>(createId, HttpStatus.OK);
    }

    // 리뷰 수정하기
    @PutMapping("/star-rates/{sheet-star-rate-id}")
    public ResponseEntity<?> updateSheetStarRate(@PathVariable("sheet-star-rate-id") Long sheetStarRateId,
            @RequestBody SheetStarRateUpdateDto sheetStarRateUpdateDto) {
        Long updateId = sheetStarRateService.updateSheetStarRate(sheetStarRateId, sheetStarRateUpdateDto);
        return new ResponseEntity<>(updateId, HttpStatus.OK);
    }

    // 리뷰 삭제하기
    @DeleteMapping("/star-rates/{star-rate-id}")
    public ResponseEntity<?> deleteSheetStarRate(@PathVariable("star-rate-id") Long starRateId) {
        sheetStarRateService.deleteSheetStarRate(starRateId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 특정 악보 리뷰 조회하기
    @GetMapping("/{sheet-id}/star-rates")
    public ResponseEntity<?> getSheetStarRateBySheetId(@PathVariable("sheet-id") Long sheetId) {
        List<SheetStarRateDto> sheetStarRateDtoList = sheetStarRateService.searchSheetStarRateBySheetId(
                sheetId);
        return new ResponseEntity<>(sheetStarRateDtoList, HttpStatus.OK);

    }

}
