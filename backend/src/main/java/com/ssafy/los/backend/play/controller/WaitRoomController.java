package com.ssafy.los.backend.play.controller;

import com.ssafy.los.backend.play.model.dto.PlayerReadyDto;
import com.ssafy.los.backend.user.model.dto.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WaitRoomController {

    @MessageMapping("/wait")
    @SendTo("/wait/socket")
    public LoginUser sendPlayer(LoginUser loginUser) throws Exception {
        // LoginUser 전송
        log.info("들어왔습니다. {}", loginUser.toString());
        return LoginUser.builder()
                .id(loginUser.getId())
                .email(loginUser.getEmail())
                .build();
    }

    @MessageMapping("/wait/ready")
    @SendTo("/wait/socket/ready")
    public PlayerReadyDto sendPlayerReady(PlayerReadyDto playerReadyDto) throws Exception {
        // LoginUser 전송
        log.info("들어왔습니다. {}", playerReadyDto.toString());
        return PlayerReadyDto.builder()
                .sender(playerReadyDto.getSender())
                .isReady(playerReadyDto.getIsReady())
                .build();
    }

    @MessageMapping("/wait/start")
    @SendTo("/wait/socket/start")
    public String sendPlayerReady(String message) throws Exception {
        // start 신호 전달
        return message;
    }

}
