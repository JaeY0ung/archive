package com.ssafy.los.backend.like.model.service;

public interface LikeService {

    void addLike(Long userId, Long sheetId);
    void deleteLike(Long userId, Long sheetId);

    int countLike(Long sheetId);
    boolean checkLike(Long userId, Long sheetId);

}
