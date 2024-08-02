package com.ssafy.los.backend.service.like;

public interface LikeSheetService {

//    void addLike(Long userId, Long sheetId);
//
//    void deleteLike(Long userId, Long sheetId);
//
//    int countLike(Long sheetId);
//
//    boolean checkLike(Long userId, Long sheetId);

    void likeSheetById(Long sheetId);

    void dislikeSheetById(Long sheetId);
}
