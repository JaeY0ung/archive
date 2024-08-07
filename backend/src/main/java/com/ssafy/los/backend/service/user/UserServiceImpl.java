package com.ssafy.los.backend.service.user;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.user.request.UserCreateDto;
import com.ssafy.los.backend.dto.user.request.UserUpdateDto;
import com.ssafy.los.backend.dto.user.response.UserProfileDto;
import com.ssafy.los.backend.exception.user.UserNotFoundException;
import com.ssafy.los.backend.exception.user.UserUpdateException;
import com.ssafy.los.backend.service.auth.PasswordService;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                .singleScore(0)
                .multiScore(0)
                .cash(0)
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
    public Long updateUser(Long userId, UserUpdateDto userUpdateDto, String uuid) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("찾을 수 없는 아이디 입니다: " + userId));

        try {
            findUser.updateProfile(userUpdateDto.getNickname(), uuid);
        } catch (Exception e) {
            throw new UserUpdateException("사용자 정보 업데이트 중 오류 발생: " + e.getMessage());
        }

        return userId;
    }

    // 회원 수정 파일 업로드
    @Override
    public String registerUserImgFile(MultipartFile profileImg) {
        try {
            return fileUploadUtil.uploadUserImg(profileImg);
        } catch (IllegalArgumentException e) {
            throw new UserUpdateException("프로필 이미지 업로드 중 오류 발생: " + e.getMessage());
        }
    }

    // 유저 프로필 조회
    @Override
    public UserProfileDto searchUserProfileByNickname(String nickname) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(
                        () -> new UserNotFoundException("찾을 수 없는 닉네임 입니다: " + nickname));

        UserProfileDto userProfileDto = UserProfileDto.builder()
                .userId(findUser.getId())
                .nickname(findUser.getNickname())
                .userImgName(findUser.getUserImg())
                .singleScore(Optional.ofNullable(findUser.getSingleScore()).orElse(0)) // null 조심
                .build();
        userProfileDto.loadUserImg(fileUploadUtil);

        return userProfileDto;
    }

    // 회원 삭제
    @Override
    public Long deleteUser(Long id) {
        userRepository.deleteById(id);
        return id;
    }

    // 이메일 기반 조회
    @Override
    public User searchUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("찾을 수 없는 이메일 입니다: " + email));
    }

    // 아이디 기반 조회
    @Override
    public User selectUserById(Long userId) {
        User user = userRepository.findUserById(userId).orElse(null);
        if (user == null) {
            log.info("repository에서 해당 id에 맞는 User를 찾을 수 없습니다: " + userId);
        }
        return user;
    }

}
