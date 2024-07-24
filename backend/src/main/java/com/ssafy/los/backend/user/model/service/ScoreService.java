package com.ssafy.los.backend.user.model.service;

public interface ScoreService {

    // 싱글 관련
    int increaseSingleScore(Long userId, int amount);
//    int decreaseSingleScore(Long userId, int amount);

    // 멀티 관련
    int increaseMultiScore(Long userId, int amount);
    int decreaseMultiScore(Long userId, int amount);

    // 스코어 가져오기
    int getUserSingleScore(Long userId);
    int getUserMultiScore(Long userId);

}
