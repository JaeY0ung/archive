package com.ssafy.los.backend.controller.user;

import com.ssafy.los.backend.dto.user.request.UserCreateDto;
import com.ssafy.los.backend.dto.user.request.UserUpdateDto;
import com.ssafy.los.backend.dto.user.response.UserDetailDto;
import com.ssafy.los.backend.dto.user.response.UserProfileDto;
import com.ssafy.los.backend.service.user.UserService;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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
    private final FileUploadUtil fileUploadUtil;

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

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateUser(
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImageFile,
            @RequestPart("userUpdateDto") UserUpdateDto userUpdateDto) {

        String uuid = UUID.randomUUID().toString();
        String fileName = null;
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            userService.registerUserImgFile(profileImageFile, uuid);
            fileName = uuid + "." + fileUploadUtil.getExtension(profileImageFile);
        }

        return new ResponseEntity<>(userService.updateUser(userUpdateDto, fileName), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser() {
        return new ResponseEntity<>(userService.deleteLoginUser(), HttpStatus.OK);
    }

    @GetMapping("/{user-nickname}")
    public ResponseEntity<?> getUserProfile(@PathVariable("user-nickname") String userNickname) {
        UserProfileDto userProfileDto = userService.searchUserProfileByNickname(userNickname);
        return new ResponseEntity<>(userProfileDto, HttpStatus.OK);
    }

    @GetMapping("/single-top-10")
    public ResponseEntity<?> getUserProfileSinglePlay() {
        List<UserDetailDto> userList = userService.searchTop10SingleScoreUsers()
                .stream()
                .map(user -> {
                    UserDetailDto userDetailDto = UserDetailDto.builder()
                            .id(user.getId())
                            .role(user.getRole())
                            .nickname(user.getNickname())
                            .email(user.getEmail())
                            .userImgName(user.getUserImg())
                            .cash(user.getCash())
                            .singleScore(user.getSingleScore())
                            .multiScore(user.getMultiScore())
                            .build();
                    userDetailDto.loadUserImg(fileUploadUtil);
                    return userDetailDto;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/multi-top-10")
    public ResponseEntity<?> getUserProfileMultiPlay() {
        List<UserDetailDto> userList = userService.searchTop10MultiScoreUsers()
                .stream()
                .map(user -> {
                    UserDetailDto userDetailDto = UserDetailDto.builder()
                            .id(user.getId())
                            .role(user.getRole())
                            .nickname(user.getNickname())
                            .email(user.getEmail())
                            .userImgName(user.getUserImg())
                            .cash(user.getCash())
                            .singleScore(user.getSingleScore())
                            .multiScore(user.getMultiScore())
                            .build();
                    userDetailDto.loadUserImg(fileUploadUtil);
                    return userDetailDto;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(userList, HttpStatus.OK);
    }
}