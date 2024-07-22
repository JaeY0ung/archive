package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.request.UserMyPageDto;
import com.ssafy.los.backend.user.model.dto.request.UserRegisterDto;
import com.ssafy.los.backend.user.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Long selectUserInfoForMyPageById(Long id);

    Long saveUser(UserRegisterDto userRegisterDto);

    Long updateUser(Long id, UserMyPageDto userMyPageDto, MultipartFile profileImg);

    Long deleteUser(Long id);

    User selectUserById(Long id);
}
