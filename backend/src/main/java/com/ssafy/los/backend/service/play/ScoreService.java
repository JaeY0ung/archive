package com.ssafy.los.backend.service.play;

public interface ScoreService {

    int increaseSingleScore(Long userId, int amount);
//    int decreaseSingleScore(Long userId, int amount);

    int increaseMultiScore(Long userId, int amount);

    int decreaseMultiScore(Long userId, int amount);

    int getUserSingleScore(Long userId);

    int getUserMultiScore(Long userId);

}
