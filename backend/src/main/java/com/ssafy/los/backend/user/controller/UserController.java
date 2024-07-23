package com.ssafy.los.backend.user.controller;

import com.ssafy.los.backend.user.model.dto.request.UserMyPageDto;
import com.ssafy.los.backend.user.model.dto.request.UserRegisterDto;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.service.AuthService;
import com.ssafy.los.backend.user.model.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    // 회원 등록
    @PostMapping
    public ResponseEntity<?> saveUser(@RequestBody UserRegisterDto requestDto) {
        log.info("---------------------------------");
        log.info("회원 등록 요청을 한 DTO = {}" , requestDto.toString());

        Long saveId = userService.saveUser(requestDto);

        return new ResponseEntity<>(saveId, HttpStatus.CREATED);
    }

    // 회원 수정
    @PostMapping(value = "/{user-id}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUser(@PathVariable("user-id") Long userId,
            @RequestPart("postData") UserMyPageDto requestDto,
            @RequestPart(value = "profileImg", required = false) MultipartFile profileImg) {
        // 내 정보만 수정 가능
        User loginUser = authService.getLoginUser();
        if (loginUser == null || !loginUser.getId().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Long updateId = userService.updateUser(userId, requestDto, profileImg);
        return new ResponseEntity<>(updateId, HttpStatus.OK);
    }

    // 회원 조회
    @GetMapping("/{user-id}")
    public ResponseEntity<?> getUser(@PathVariable("user-id") Long userId) {
        // TODO : 로그인 유저랑 비교하여 내 정보 찾아올 때와 다른 유저 찾아올 때로 로직 나누기
        Long getId = userService.selectUserInfoForMyPageById(userId);
        return new ResponseEntity<>(getId, HttpStatus.OK);
    }

    // 회원 삭제
    @DeleteMapping("/{user-id}")
    public ResponseEntity<?> deleteUser(@PathVariable("user-id") Long userId) {
        // TODO : 로그인 유저랑 비교하여 내 정보 삭제 가능하게 하기
        Long deleteId = userService.deleteUser(userId);
        return new ResponseEntity<>(deleteId, HttpStatus.OK);
    }
}
