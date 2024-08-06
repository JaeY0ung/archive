package com.ssafy.los.backend.controller.user;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.user.request.UserCreateDto;
import com.ssafy.los.backend.dto.user.request.UserUpdateDto;
import com.ssafy.los.backend.dto.user.response.UserProfileDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.user.UserService;
import java.io.IOException;
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
        return new ResponseEntity<>(isDuplicated, HttpStatus.OK);
    }

    // 회원 닉네임 중복 여부 체크
    @GetMapping("/check-nickname")
    public ResponseEntity<?> validateNickname(@RequestParam("nickname") String nickname) {
        boolean isDuplicated = userService.validateNickname(nickname);
        return new ResponseEntity<>(isDuplicated, HttpStatus.OK);
    }

    // 회원 정보 수정
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUser(
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart("nickname") String nickname) throws IOException {

        User loginUser = authService.getLoginUser();

        // 파일 처리
        String uuid = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            uuid = userService.registerUserImgFile(profileImage);
        }

        // 닉네임 처리
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .nickname(nickname)
                .build();

        Long updatedId = userService.updateUser(loginUser.getId(), userUpdateDto, uuid);

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