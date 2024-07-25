package com.ssafy.los.backend.user.model.service;

import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ScoreServiceImpl implements ScoreService{

    private final UserRepository userRepository;

    @Override
    public int increaseSingleScore(Long userId, int amount) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id = " + userId));
        findUser.increaseSingleScore(amount);
        return findUser.getSingleScore();
    }

    @Override
    public int increaseMultiScore(Long userId, int amount) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id = " + userId));
        findUser.increaseMultiScore(amount);
        return findUser.getSingleScore();
    }

    @Override
    public int decreaseMultiScore(Long userId, int amount) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id = " + userId));
        if (findUser.getSingleScore() >= amount) {
            findUser.decreaseMultiScore(amount);
        } else {
            findUser.decreaseMultiScore(findUser.getMultiScore()); // amount 보다 작으면 해당 값 전체 삭제
        }

        return findUser.getSingleScore();
    }

    @Override
    public int getUserSingleScore(Long userId) {

        return userRepository.findById(userId)
                .map(User::getSingleScore)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id = " + userId));
    }

    @Override
    public int getUserMultiScore(Long userId) {
        return userRepository.findById(userId)
                .map(User::getSingleScore)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. id = " + userId));
    }
}
