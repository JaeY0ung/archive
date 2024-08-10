package com.ssafy.los.backend.controller.play;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultAfterDto;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultBeforeDto;
import com.ssafy.los.backend.dto.play.response.MultiPlayResultListDto;
import com.ssafy.los.backend.dto.user.response.ScoreDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.play.MultiPlayService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/play")
public class MultiPlayController {

    private final MultiPlayService multiPlayService;
    private final AuthService authService;

    @MessageMapping("/{roomId}")
    @SendTo("/socket/{roomId}")
    public ScoreDto sendScore(ScoreDto scoreDto) throws Exception {
        return scoreDto;
    }

    @MessageMapping("/start/{roomId}")
    @SendTo("/start/socket/{roomId}")
    public String sendRecordStart(ScoreDto scoreDto) throws Exception {
        return "start";
    }

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

    // 로그인한 유저에 대해서 멀티 결과 기록들을 조회한다.
    @GetMapping
    public ResponseEntity<?> getMultiResultAll() {
        User loginUser = authService.getLoginUser();
        List<MultiPlayResultListDto> multiPlayResultList = multiPlayService.getMultiPlayResultList(
                loginUser);

        return new ResponseEntity<>(multiPlayResultList, HttpStatus.OK);
    }

    // 멀티 기록 삭제
    @DeleteMapping("/multi-result-id")
    public ResponseEntity<?> deleteSinglePlayResult(
            @PathVariable("single-result-id") Long singleResultId) {
        Long deletedSingleResultId = multiPlayService.removeMultiPlayResult(singleResultId);
        return new ResponseEntity<>(deletedSingleResultId, HttpStatus.OK);
    }
}
