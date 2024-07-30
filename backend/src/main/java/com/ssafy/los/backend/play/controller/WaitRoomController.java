package com.ssafy.los.backend.play.controller;

import com.ssafy.los.backend.play.model.dto.PlayerReadyDto;
import com.ssafy.los.backend.play.model.dto.PlayerStartDto;
import com.ssafy.los.backend.user.model.dto.LoginUser;
import com.ssafy.los.backend.user.model.service.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class WaitRoomController {

    private final UserServiceImpl userServiceImpl;

    public WaitRoomController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

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
                .senderNickname(playerReadyDto.getSenderNickname())
                .isReady(playerReadyDto.getIsReady())
                // Todo: 닉네임으로 유저 정보를 가져오는 메서드 활용해서, 이미지 이름을 가져 오는 것. 만들어야 함.
                // Todo: 이미지 이름으로 이미지 파일을 가져오는 것. 만들어야 함.
//                .profileImg(null)
                .build();
    }

    @MessageMapping("/wait/start")
    @SendTo("/wait/socket/start")
    public PlayerStartDto sendPlayerReady(PlayerStartDto playerStartDto) throws Exception {
        // start 신호 전송

        System.out.println("메시지 전달받음");
        log.info("들어왔습니다. {}", playerStartDto.toString());

        return PlayerStartDto.builder()
                .type(playerStartDto.getType())
                .sender(playerStartDto.getSender())
                .content(playerStartDto.getContent())
                .build();
    }

}
