package com.ssafy.los.backend.service.like;

public interface LikeSheetService {

    boolean likeSheetById(Long sheetId);

    boolean dislikeSheetById(Long sheetId);
}
