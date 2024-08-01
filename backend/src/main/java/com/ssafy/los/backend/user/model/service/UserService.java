package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.request.UserCreateDto;
import com.ssafy.los.backend.user.model.dto.request.UserUpdateDto;
import com.ssafy.los.backend.user.model.dto.response.UserProfileDto;
import com.ssafy.los.backend.user.model.entity.User;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Long saveUser(UserCreateDto userCreateDto);

    Boolean validateEmail(String email);

    Boolean validateNickname(String email);

    Long updateUser(Long id, UserUpdateDto userUpdateDto, String uuid);

    Long deleteUser(Long id);

    String saveUserImgFile(MultipartFile userImg) throws IOException;

    User searchUserByEmail(String email);

    UserProfileDto searchUserProfileByNickname(String nickname);
}