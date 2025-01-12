package com.ssafy.los.backend.controller.play;

import com.ssafy.los.backend.dto.play.PlayerReadyDto;
import com.ssafy.los.backend.dto.play.PlayerStartDto;
import com.ssafy.los.backend.dto.user.LoginUser;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.user.UserService;
import com.ssafy.los.backend.service.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WaitRoomController {

    private final UserServiceImpl userServiceImpl;

    private final AuthService authService;
    private final UserService userService;

    @MessageMapping("/wait/{roomId}")
    @SendTo("/wait/socket/{roomId}")
    public LoginUser sendPlayer(LoginUser loginUser) throws Exception {
        // LoginUser 전송

        return LoginUser.builder()
                .id(loginUser.getId())
                .nickname(loginUser.getNickname())
                .userImg(loginUser.getUserImg())
                .build();
    }

    @MessageMapping("/wait/ready/{roomId}")
    @SendTo("/wait/socket/ready/{roomId}")
    public PlayerReadyDto sendPlayerReady(PlayerReadyDto playerReadyDto) throws Exception {
        return PlayerReadyDto.builder()
                .sender(playerReadyDto.getSender())
                .isReady(playerReadyDto.getIsReady())
                // Todo: 닉네임으로 유저 정보를 가져오는 메서드 활용해서, 이미지 이름을 가져 오는 것. 만들어야 함.
                // Todo: 이미지 이름으로 이미지 파일을 가져오는 것. 만들어야 함.
//                .profileImg(null)
                .build();
    }

    @MessageMapping("/wait/start/{roomId}")
    @SendTo("/wait/socket/start/{roomId}")
    public PlayerStartDto sendPlayerReady(PlayerStartDto playerStartDto) throws Exception {
        log.info("대기방에서 시작버튼을 눌렀을 때 : {}", playerStartDto.toString());
        return playerStartDto;
    }

}
