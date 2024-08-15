package com.ssafy.los.backend.controller.user;

import com.ssafy.los.backend.dto.user.response.OnlineUserDto;
import com.ssafy.los.backend.service.user.UserService;
import com.ssafy.los.backend.service.user.UserStatusService;
import com.ssafy.los.backend.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserStatusController {

    private static final Logger LOGGER = Logger.getLogger(UserStatusController.class.getName());
    private final UserStatusService userStatusService;
    private final UserService userService;
    private final FileUploadUtil fileUploadUtil;

    @GetMapping("/is-online/{id}")
    public ResponseEntity<Boolean> isUserOnline(@PathVariable Long id) {
        boolean isOnline = userStatusService.isUserOnline(id);
        LOGGER.info("User " + id + " is online: " + isOnline);
        return new ResponseEntity<>(isOnline, HttpStatus.OK);
    }

    @GetMapping("/online-users")
    public ResponseEntity<List<OnlineUserDto>> getOnlineUsers() {
        Set<String> onlineUserIds = userStatusService.getOnlineUsers();
        LOGGER.info("userStatusService.getOnlineUsers() 결과: " + onlineUserIds);

        List<OnlineUserDto> onlineUserDtoList = onlineUserIds.stream()
                .map(userId -> userService.selectUserById(Long.parseLong(userId)))
                .filter(Objects::nonNull) // null 값을 필터링
                .map(user -> OnlineUserDto.builder()
                        .id(user.getId())
                        .userImgName(user.getUserImg())
                        .nickname(user.getNickname())
                        .singleScore(user.getSingleScore())
                        .build())
                .peek(user -> user.loadUserImg(fileUploadUtil))
                .collect(Collectors.toList());

        return new ResponseEntity<>(onlineUserDtoList, HttpStatus.OK);
    }
}