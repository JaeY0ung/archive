package com.ssafy.los.backend.service.user;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.dto.user.request.UserCreateDto;
import com.ssafy.los.backend.dto.user.request.UserUpdateDto;
import com.ssafy.los.backend.dto.user.response.UserProfileDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Long saveUser(UserCreateDto userCreateDto);

    Boolean validateEmail(String email);

    Boolean validateNickname(String email);

    User updateUser(UserUpdateDto userUpdateDto, String fileName);

    Long deleteLoginUser();

    void registerUserImgFile(MultipartFile userImg, String uuid);

    User searchUserByEmail(String email);

    UserProfileDto searchUserProfileByNickname(String nickname);

    User selectUserById(Long userId);

    List<User> searchTop10SingleScoreUsers();

    List<User> searchTop10MultiScoreUsers();
}