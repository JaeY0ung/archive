package com.ssafy.los.backend.service.user;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.user.request.UserCreateDto;
import com.ssafy.los.backend.dto.user.request.UserUpdateDto;
import com.ssafy.los.backend.dto.user.response.UserProfileDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Long saveUser(UserCreateDto userCreateDto);

    Boolean validateEmail(String email);

    Boolean validateNickname(String email);

    User updateUser(UserUpdateDto userUpdateDto, String uuid);

    Long deleteLoginUser();

    void registerUserImgFile(MultipartFile userImg, String uuid);

    User searchUserByEmail(String email);

    UserProfileDto searchUserProfileByNickname(String nickname);

    User selectUserById(Long userId);
}