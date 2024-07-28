package com.ssafy.los.backend.like.model.service;

import com.ssafy.los.backend.like.model.entity.LikeSheet;
import com.ssafy.los.backend.like.model.repository.LikeRepository;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.service.SheetService;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeSheetServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final AuthService authService;
    private final SheetService sheetService;

    @Override
    public void likeSheetById(Long sheetId) {
        User user = authService.getLoginUser();
        Sheet sheet = sheetService.selectById(sheetId);
        if (isExistByUserAndSheet(user, sheet)) {
            throw new IllegalArgumentException("이미 좋아요한 악보입니다");
        }
        LikeSheet likeSheet = LikeSheet.builder().user(user).sheet(sheet).build();
        likeRepository.save(likeSheet);
    }

    @Override
    public void dislikeSheetById(Long sheetId) {
        User user = authService.getLoginUser();
        Sheet sheet = sheetService.selectById(sheetId);
        if (!isExistByUserAndSheet(user, sheet)) {
            throw new IllegalArgumentException("이미 좋아요 해제한 악보입니다");
        }
        likeRepository.deleteByUserEqualsAndSheetEquals(user, sheet);
    }

    public boolean isExistByUserAndSheet(User user, Sheet sheet) {
        return likeRepository.findByUserEqualsAndSheetEquals(user, sheet).isPresent();
    }
}
