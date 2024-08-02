package com.ssafy.los.backend.service.user;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.user.request.UserCreateDto;
import com.ssafy.los.backend.dto.user.request.UserUpdateDto;
import com.ssafy.los.backend.dto.user.response.UserProfileDto;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Long saveUser(UserCreateDto userCreateDto);

    Boolean validateEmail(String email);

    Boolean validateNickname(String email);

    Long updateUser(Long id, UserUpdateDto userUpdateDto, String uuid);

    Long deleteUser(Long id);

    String registerUserImgFile(MultipartFile userImg) throws IOException;

    User searchUserByEmail(String email);

    UserProfileDto searchUserProfileByNickname(String nickname);

    User selectUserById(Long userId);
}