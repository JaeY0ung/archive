package com.ssafy.los.backend.controller.play;

import com.ssafy.los.backend.dto.user.response.ScoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/scores")
public class ScoreController {

    @MessageMapping("/wait/play/{roomId}")
    @SendTo("/wait/play/{roomId}")
    public ScoreDto sendScore(@PathVariable("roomId") String roomId, ScoreDto scoreDto) throws Exception {
        log.info("sendScore: {}", scoreDto);
        return scoreDto; // 수정
    }

}
