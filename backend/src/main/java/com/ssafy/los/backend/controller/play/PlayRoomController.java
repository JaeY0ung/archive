package com.ssafy.los.backend.controller.play;

import com.ssafy.los.backend.dto.play.PlayerEndDto;
import com.ssafy.los.backend.dto.play.PlayerStartDto;
import com.ssafy.los.backend.dto.play.request.SendIdDto;
import com.ssafy.los.backend.dto.user.response.ScoreDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.play.MultiPlayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PlayRoomController {

    private final MultiPlayService multiPlayService;
    private final AuthService authService;

    @MessageMapping("/play/{roomId}")
    @SendTo("/play/socket/{roomId}")
    public ScoreDto sendScore(ScoreDto scoreDto) throws Exception {
        log.info("점수를 받았습니다. : {} : ", scoreDto.toString());
        return scoreDto;
    }

    @MessageMapping("/play/start/{roomId}")
    @SendTo("/play/start/socket/{roomId}")
    public PlayerStartDto sendRecordStart(PlayerStartDto playerStartDto) throws Exception {
        log.info("시작 신호를 받았습니다. : {}", playerStartDto.toString());
        return playerStartDto;
    }

    @MessageMapping("/play/end/{roomId}")
    @SendTo("/play/end/socket/{roomId}")
    public PlayerEndDto sendEndSign(PlayerEndDto playerEndDto) throws Exception {
        log.info("종료 신호를 받았습니다. : {}", playerEndDto.toString());
        return playerEndDto;
    }

    @MessageMapping("/play/end/quit/{roomId}")
    @SendTo("/play/end/quit/socket/{roomId}")
    public PlayerEndDto sendQuitEndSign(PlayerEndDto playerEndDto) throws Exception {
        log.info("중간 탈주로 인한 종료 신호를 받았습니다. : {}", playerEndDto.toString());
        return playerEndDto;
    }

    @MessageMapping("/play/id/{roomId}")
    @SendTo("/play/id/socket/{roomId}")
    public SendIdDto sendId(SendIdDto sendIdDto) throws Exception {
        log.info("id 전송. : {}", sendIdDto.toString());
        return sendIdDto;
    }

}
