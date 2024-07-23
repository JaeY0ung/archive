package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.request.UserRegisterDto;
import com.ssafy.los.backend.user.model.dto.request.UserUpdateDto;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public Long selectUserInfoForMyPageById(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 user id 입니다."));
        return id;
    }

    // 회원 등록
    @Override
    public Long saveUser(UserRegisterDto userRegisterDto) {
        String hashPwd = bCryptPasswordEncoder.encode(userRegisterDto.getPassword());
        String role = "ROLE_USER";
        User user = userRegisterDto.toEntity(hashPwd, role);
        return userRepository.save(user).getId();
    }

    // 회원 수정
    @Override
    public Long updateUser(Long id, UserUpdateDto userUpdateForm, String uuid) {

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 아이디 압니다. " + id));

        findUser.updateProfile(userUpdateForm.getNickname(), uuid);
        return id;
    }

    // 회원 수정 파일 업로드
    @Override
    public String saveUserImgFile(MultipartFile profileImg) throws IOException {
        return fileUploadUtil.uploadUserImg(profileImg);
    }

    // 회원 삭제
    @Override
    public Long deleteUser(Long id) {
        userRepository.deleteById(id);
        return id;
    }

    @Override
    public User selectUserById(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 user id 입니다."));
    }


}
