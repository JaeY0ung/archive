package com.ssafy.los.backend.service.like;

import com.ssafy.los.backend.domain.entity.LikeSheet;
import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.like.LikeRepository;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.sheet.SheetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeSheetServiceImpl implements LikeSheetService {

    private final LikeRepository likeRepository;
    private final AuthService authService;
    private final SheetService sheetService;

    @Override
    public boolean likeSheetById(Long sheetId) {
        User loginUser = authService.getLoginUser();

        if (loginUser == null) {
            return false;
        }

        Sheet sheet = sheetService.searchById(sheetId);

        if (isExistByUserAndSheet(loginUser, sheet)) {
            throw new IllegalArgumentException("이미 좋아요한 악보입니다");
        }
        LikeSheet likeSheet = LikeSheet.builder()
                .user(loginUser)
                .sheet(sheet)
                .build();
        likeRepository.save(likeSheet);
        return true;
    }

    @Override
    public boolean dislikeSheetById(Long sheetId) {
        User loginUser = authService.getLoginUser();

        if (loginUser == null) {
            return false;
        }

        Sheet sheet = sheetService.searchById(sheetId);

        if (!isExistByUserAndSheet(loginUser, sheet)) {
            throw new IllegalArgumentException("이미 좋아요 해제한 악보입니다");
        }
        likeRepository.deleteByUserEqualsAndSheetEquals(loginUser, sheet);
        return true;
    }

    public boolean isExistByUserAndSheet(User user, Sheet sheet) {
        return likeRepository.findByUserEqualsAndSheetEquals(user, sheet).isPresent();
    }

}
