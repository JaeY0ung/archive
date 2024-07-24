package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService{

    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    public Long updateSingleScore(Long id) {
        User loginUser = authService.getLoginUser();

        return 0L;
    }

    @Override
    public Long updateMultiScore(Long id) {
        return 0L;
    }
}
