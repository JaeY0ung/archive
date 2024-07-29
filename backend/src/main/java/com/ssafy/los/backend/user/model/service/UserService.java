package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.request.UserRegisterDto;
import com.ssafy.los.backend.user.model.dto.request.UserUpdateDto;
import com.ssafy.los.backend.user.model.entity.User;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Long selectUserInfoForMyPageById(Long id);

    Boolean validateEmail(String email);

    Long saveUser(UserRegisterDto userRegisterDto);

    Long updateUser(Long id, UserUpdateDto userUpdateForm, String uuid);

    Long deleteUser(Long id);

    User selectUserById(Long id);

    String saveUserImgFile(MultipartFile profileImg) throws IOException;

    User selectUserByEmail(String email);
}
