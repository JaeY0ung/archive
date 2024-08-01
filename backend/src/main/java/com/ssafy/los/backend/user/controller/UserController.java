package com.ssafy.los.backend.user.controller;

import com.ssafy.los.backend.user.model.dto.request.UserCreateDto;
import com.ssafy.los.backend.user.model.dto.request.UserUpdateDto;
import com.ssafy.los.backend.user.model.dto.response.UserProfileDto;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.service.AuthService;
import com.ssafy.los.backend.user.model.service.UserService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<?> createUser(@RequestBody UserCreateDto userCreateDto) {
        Long createId = userService.saveUser(userCreateDto);
        return new ResponseEntity<>(createId, HttpStatus.CREATED);
    }

    // 회원 이메일 중복 여부 체크
    @GetMapping("/check-email")
    public ResponseEntity<?> validateEmail(@RequestParam("email") String email) {
        boolean isDuplicated = userService.validateEmail(email);
        log.info("이메일 중복 여부 검사 data = {}", isDuplicated);
        return new ResponseEntity<>(isDuplicated, HttpStatus.OK);
    }

    // 회원 닉네임 중복 여부 체크
    @GetMapping("/check-nickname")
    public ResponseEntity<?> validateNickname(@RequestParam("nickname") String nickname) {
        boolean isDuplicated = userService.validateNickname(nickname);
        log.info("닉네임 중복 여부 검사 data = {}", isDuplicated);
        return new ResponseEntity<>(isDuplicated, HttpStatus.OK);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
            "multipart/form-data"})
    public ResponseEntity<?> updateUser(
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart("nickname") String nickname) throws IOException {

        User loginUser = authService.getLoginUser();

        // TODO : 로그인 유저 로직 추가

        // 파일 처리
        String uuid = null;
        if (files.size() == 1) {
            uuid = userService.saveUserImgFile(files.get(0));
        }

        // 닉네임 처리
        UserUpdateDto.UserUpdateDtoBuilder updateFormBuilder = UserUpdateDto.builder();
        if (nickname != null) {
            updateFormBuilder.nickname(nickname);
        }
        UserUpdateDto userUpdateForm = updateFormBuilder.build();

        Long updatedId = userService.updateUser(loginUser.getId(), userUpdateForm, uuid);
        return new ResponseEntity<>(updatedId, HttpStatus.OK);

    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<?> deleteUser(@PathVariable("user-id") Long userId) {
        User loginUser = authService.getLoginUser();
        if (!userId.equals(loginUser.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Long deleteId = userService.deleteUser(userId);
        return new ResponseEntity<>(deleteId, HttpStatus.OK);

    }

    // 회원 조회
    @GetMapping("/{user-nickname}")
    public ResponseEntity<?> getUser(@PathVariable("user-nickname") String userNickname) {
        UserProfileDto userProfileDto = userService.searchUserProfileByNickname(userNickname);
        return new ResponseEntity<>(userProfileDto, HttpStatus.OK);
    }

}