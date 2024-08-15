package com.ssafy.los.backend.service.user;

import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.user.request.UserCreateDto;
import com.ssafy.los.backend.dto.user.request.UserUpdateDto;
import com.ssafy.los.backend.dto.user.response.UserProfileDto;
import com.ssafy.los.backend.exception.user.UserNotFoundException;
import com.ssafy.los.backend.exception.user.UserUpdateException;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.auth.PasswordService;
import com.ssafy.los.backend.util.FileUploadUtil;
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
    private final AuthService authService;

    // 회원 등록
    @Override
    public Long saveUser(UserCreateDto userCreateDto) {
        String hashPwd = passwordService.encode(userCreateDto.getPassword());
        String role = "ROLE_USER"; // 기본적으로 USER 권한 추가
        User user = User.builder()
                .email(userCreateDto.getEmail())
                .nickname(userCreateDto.getNickname())
//                .birthDate(userCreateDto.getBirthDate())
//                .gender(userCreateDto.getGender())
                .pwdHash(hashPwd)
                .role(role)
                .singleScore(0)
                .multiScore(1000)
                .cash(0)
                .build();
        return userRepository.save(user).getId();
    }

    // 이메일 중복 확인
    @Override
    public Boolean validateEmail(String email) {
        return userRepository.existsByEmailAndDeletedAtNull(email);
    }

    // 닉네임 중복 확인
    @Override
    public Boolean validateNickname(String nickname) {
        return userRepository.existsByNicknameAndDeletedAtNull(nickname);
    }

    // 회원 수정
    @Override
    public User updateUser(UserUpdateDto userUpdateDto, String fileName) {
        User loginUser = authService.getLoginUser();
        if (loginUser == null) {
            throw new UserUpdateException("로그인 유저 없음");
        }

        try {
            loginUser.updateProfile(userUpdateDto.getNickname(), fileName);
        } catch (Exception e) {
            throw new UserUpdateException("사용자 정보 업데이트 중 오류 발생: " + e.getMessage());
        }

        return loginUser;
    }

    // 회원 수정 파일 업로드
    @Override
    public void registerUserImgFile(MultipartFile imgFile, String uuid) {
        try {
            fileUploadUtil.uploadUserImg(imgFile, uuid);
        } catch (IllegalArgumentException e) {
            throw new UserUpdateException("프로필 이미지 업로드 중 오류 발생: " + e.getMessage());
        }
    }

    // 유저 프로필 조회
    @Override
    public UserProfileDto searchUserProfileByNickname(String nickname) {
        User findUser = userRepository.findByNicknameAndDeletedAtNull(nickname)
                .orElseThrow(
                        () -> new UserNotFoundException("찾을 수 없는 닉네임 입니다: " + nickname));

        UserProfileDto userProfileDto = UserProfileDto.builder()
                .userId(findUser.getId())
                .nickname(findUser.getNickname())
                .userImgName(findUser.getUserImg())
                .singleScore(findUser.getSingleScore() != null ? findUser.getSingleScore() : 0)
                .multiScore(findUser.getMultiScore() != null ? findUser.getMultiScore() : 0)
                .build();
        userProfileDto.loadUserImg(fileUploadUtil);

        return userProfileDto;
    }

    // 회원 삭제
    @Override
    public Long deleteLoginUser() {
        User loginUser = authService.getLoginUser();
        if (loginUser == null) {
            return 0L;
        }
        userRepository.deleteById(loginUser.getId());
        return loginUser.getId();
    }

    // 이메일 기반 조회
    @Override
    public User searchUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtNull(email)
                .orElseThrow(() -> new UserNotFoundException("찾을 수 없는 이메일 입니다: " + email));
    }

    // 아이디 기반 조회
    @Override
    public User selectUserById(Long userId) {
        User user = userRepository.findUserByIdAndDeletedAtNull(userId).orElse(null);
        if (user == null) {
            log.info("repository에서 해당 id에 맞는 User를 찾을 수 없습니다: " + userId);
        }
        return user;
    }

}
