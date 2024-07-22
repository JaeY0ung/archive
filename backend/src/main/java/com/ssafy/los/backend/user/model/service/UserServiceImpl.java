package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.dto.request.UserMyPageDto;
import com.ssafy.los.backend.user.model.dto.request.UserRegisterDto;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Long selectUserInfoForMyPageById(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 user id 입니다."));
        return id;
    }

    // 회원 등록
    @Override
    public Long saveUser(UserRegisterDto userRegisterDto) {
        User user = userRegisterDto.toEntity();
        return userRepository.save(user).getId();
    }

    // 회원 수정
    @Override
    public Long updateUser(Long id, UserMyPageDto userMyPageDto, MultipartFile image) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("업데이트할 유저가 없습니다. id = " + id));

        // 이미지 저장 로직
        String updateImgPath = "";
        // service 호출 및 저장

        user.update(userMyPageDto.getEmail(), updateImgPath);
        return id;
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
