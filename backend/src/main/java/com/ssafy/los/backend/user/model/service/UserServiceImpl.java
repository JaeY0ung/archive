package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.config.PasswordService;
import com.ssafy.los.backend.user.model.dto.request.UserCreateDto;
import com.ssafy.los.backend.user.model.dto.request.UserUpdateDto;
import com.ssafy.los.backend.user.model.dto.response.UserProfileDto;
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
    private final PasswordService passwordService;

    // 회원 등록
    @Override
    public Long saveUser(UserCreateDto userCreateDto) {
        String hashPwd = passwordService.encode(userCreateDto.getPassword());
        String role = "ROLE_USER"; // 기본적으로 USER 권한 추가
        User user = User.builder()
                .email(userCreateDto.getEmail())
                .birthDate(userCreateDto.getBirthDate())
                .nickname(userCreateDto.getNickname())
                .gender(userCreateDto.getGender())
                .pwdHash(hashPwd)
                .role(role)
                .build();
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
    public Long updateUser(Long id, UserUpdateDto userUpdateDto, String uuid) {
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 아이디 입니다. " + id));

        findUser.updateProfile(userUpdateDto.getNickname(), uuid);
        return id;
    }

    // 회원 수정 파일 업로드
    @Override
    public String registerUserImgFile(MultipartFile profileImg) throws IOException {
        return fileUploadUtil.uploadUserImg(profileImg);
    }

    // 유저 프로필 조회
    @Override
    public UserProfileDto searchUserProfileByNickname(String nickname) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(
                        () -> new IllegalArgumentException("nickname에 해당하는 유저가 없습니다. " + nickname));

        UserProfileDto userProfileDto = UserProfileDto.builder()
                .userId(findUser.getId())
                .nickname(findUser.getNickname())
                .userImg(findUser.getUserImg())
                .singleScore(findUser.getSingleScore())
                .build();

        return userProfileDto;
    }

    // 회원 삭제
    @Override
    public Long deleteUser(Long id) {
        userRepository.deleteById(id);
        return id;
    }

    @Override
    public User searchUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("email에 해당하는 유저가 없습니다. " + email));
    }

    @Override
    public User selectUserById(Long userId) {
        return userRepository.findUserById(userId).orElse(null);
    }

}
