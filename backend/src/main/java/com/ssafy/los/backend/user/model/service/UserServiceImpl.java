package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.config.PasswordService;
import com.ssafy.los.backend.user.model.dto.request.UserRegisterDto;
import com.ssafy.los.backend.user.model.dto.request.UserUpdateDto;
import com.ssafy.los.backend.user.model.dto.response.UserProfileResponseDto;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import com.ssafy.los.backend.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileUploadUtil fileUploadUtil;
    //    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PasswordService passwordService;

    // 회원 등록
    @Override
    public Long saveUser(UserRegisterDto userRegisterDto) {
        String hashPwd = passwordService.encode(userRegisterDto.getPassword());
        String role = "ROLE_USER";
        User user = userRegisterDto.toEntity(hashPwd, role);
        return userRepository.save(user).getId();
    }

    // 이메일 중복 확인
    @Override
    public Boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 닉네임 중복 확인
    @Override
    public Boolean validateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 회원 수정
    @Override
    public Long updateUser(Long id, UserUpdateDto userUpdateForm, String uuid) {

        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 아이디 입니다. " + id));

        findUser.updateProfile(userUpdateForm.getNickname(), uuid);
        return id;
    }

    // 회원 수정 파일 업로드
    @Override
    public String saveUserImgFile(MultipartFile profileImg) throws IOException {
        return fileUploadUtil.uploadUserImg(profileImg);
    }

    // 유저 프로필 조회
    @Override
    public UserProfileResponseDto searchUserPofile(String nickname) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("nickname에 해당하는 유저가 없습니다. " + nickname));

        UserProfileResponseDto userProfileResponseDto = UserProfileResponseDto.builder()
                .userId(findUser.getId())
                .nickname(findUser.getNickname())
                .userImg(findUser.getUserImg())
                .singleScore(findUser.getSingleScore())
                .build();

        log.info("해당하는 프로필을 제공합니다. = {}", userProfileResponseDto.toString());
        return userProfileResponseDto;
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
                .orElseThrow(() -> new IllegalArgumentException("id에 해당하는 유저가 없습니다. " + id));
    }

    @Override
    public User selectUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("email에 해당하는 유저가 없습니다. " + email));
    }

}
