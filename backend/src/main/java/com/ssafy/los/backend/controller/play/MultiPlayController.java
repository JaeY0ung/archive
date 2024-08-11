package com.ssafy.los.backend.controller.play;

import com.ssafy.los.backend.dto.play.request.MultiPlayResultAfterDto;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultBeforeDto;
import com.ssafy.los.backend.dto.play.response.MultiPlayResultProfileDto;
import com.ssafy.los.backend.dto.user.response.ScoreDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.play.MultiPlayService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/play")
public class MultiPlayController {

    private final MultiPlayService multiPlayService;

    // 게임 시작 시, 멀티 결과 생성
    @PostMapping
    public ResponseEntity<?> saveMultiPlayResult(MultiPlayResultBeforeDto multiResultBeforeDto) {
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
