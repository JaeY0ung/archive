package com.ssafy.los.backend.sheet.controller;

import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateCreateDto;
import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateUpdateDto;
import com.ssafy.los.backend.sheet.model.dto.response.SheetStarRateResponseDto;
import com.ssafy.los.backend.sheet.model.service.SheetStarRateService;
import com.ssafy.los.backend.user.model.entity.User;
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
@RequestMapping("/sheets/star-rates")
public class SheetStarRateController {

    private final SheetStarRateService sheetStarRateService;
    private final AuthService authService;


    // 리뷰 생성하기
    @PostMapping
    public ResponseEntity<?> saveSheetStarRate(@RequestBody SheetStarRateCreateDto sheetStarRateCreateDto) {
        User user = authService.getLoginUser();
        Long findId = sheetStarRateService.saveStarRate(sheetStarRateCreateDto, user);
        return new ResponseEntity<>(findId, HttpStatus.OK);
    }

    // 리뷰 수정하기
    @PutMapping("/{sheet-star-rate-id}")
    public ResponseEntity<?> updateStarRate(@PathVariable Long sheetStarRateId, @RequestBody SheetStarRateUpdateDto sheetStarRateUpdateDto) {
        Long updateId = sheetStarRateService.updateStarRate(sheetStarRateId, sheetStarRateUpdateDto);
        return new ResponseEntity<>(updateId, HttpStatus.OK);
    }

    // 리뷰 삭제하기
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStarRate(@PathVariable Long id) {
        sheetStarRateService.deleteStarRate(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 리뷰 하나 가져오기
//    @GetMapping("/{id}")
//    public ResponseEntity<?> findStarRate(@PathVariable Long id) {
//        SheetStarRate sheetStarRate = sheetStarRateService.findStarRateById(id);
//        return new ResponseEntity<>(sheetStarRate, HttpStatus.OK);
//    }

    // 특정 악보 리뷰 조회하기
    @GetMapping("/{sheet-id}")
    public ResponseEntity<?> findStarRateBySheetId(@PathVariable("sheet-id") Long sheetId) {
        List<SheetStarRateResponseDto> SheetStarRateResponseDtoList = sheetStarRateService.findStarRateBySheetId(sheetId);
        return new ResponseEntity<>(SheetStarRateResponseDtoList, HttpStatus.OK);

    }

}
