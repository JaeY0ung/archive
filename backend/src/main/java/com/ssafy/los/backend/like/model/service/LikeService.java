package com.ssafy.los.backend.like.model.service;

public interface LikeService {

    void likeSheetById(Long sheetId);

    void dislikeSheetById(Long sheetId);
}
