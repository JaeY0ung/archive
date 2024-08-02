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
    public void likeSheetById(Long sheetId) {
        User user = authService.getLoginUser();
        Sheet sheet = sheetService.searchById(sheetId);

        if (isExistByUserAndSheet(user, sheet)) {
            throw new IllegalArgumentException("이미 좋아요한 악보입니다");
        }
        LikeSheet likeSheet = LikeSheet.builder()
                .user(user)
                .sheet(sheet)
                .build();
        likeRepository.save(likeSheet);
    }

    @Override
    public void dislikeSheetById(Long sheetId) {
        User user = authService.getLoginUser();
        Sheet sheet = sheetService.searchById(sheetId);
        if (!isExistByUserAndSheet(user, sheet)) {
            throw new IllegalArgumentException("이미 좋아요 해제한 악보입니다");
        }
        likeRepository.deleteByUserEqualsAndSheetEquals(user, sheet);
    }

    public boolean isExistByUserAndSheet(User user, Sheet sheet) {
        return likeRepository.findByUserEqualsAndSheetEquals(user, sheet).isPresent();
    }

    //    @Override
//    public void addLike(Long userId, Long sheetId) {
//        User findUser = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저 아이디가 없습니다. id = " + userId));
//        Sheet findSheet = sheetRepository.findById(sheetId)
//            .orElseThrow(() -> new IllegalArgumentException("해당하는 악보가 없습니다. id = " + sheetId));
//
//        // 이미 좋아요가 존재하는지 확인
//        if (likeRepository.existsByUserAndSheet(findUser, findSheet)) {
//            throw new IllegalStateException("이미 좋아요를 누른 악보입니다.");
//        }
//
//        LikeSheet likeSheet = LikeSheet.builder()
//                .user(findUser)
//                .sheet(findSheet)
//                .build();

//    @Override
//    public void deleteLike(Long userId, Long sheetId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저 아이디가 없습니다. id = " + userId));
//        Sheet sheet = sheetRepository.findById(sheetId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 악보가 없습니다. id = " + sheetId));
//
//        LikeSheet like = likeRepository.findByUserAndSheet(user, sheet)
//                        .orElseThrow(() -> new IllegalArgumentException("해당하는 좋아요가 없습니다."));
//        likeRepository.delete(like);
//    }

//    @Override
//    public int countLike(Long sheetId) {
//        Sheet sheet = sheetRepository.findById(sheetId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 악보가 없습니다. id = " + sheetId));
//
//        return likeRepository.findBySheet(sheet).size();
//    }

//    @Override
//    public boolean checkLike(Long userId, Long sheetId) {
//        User findUser = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저 아이디가 없습니다. id = " + userId));
//        Sheet findSheet = sheetRepository.findById(sheetId)
//                .orElseThrow(() -> new IllegalArgumentException("해당하는 악보가 없습니다. id = " + sheetId));
//
//        if (likeRepository.existsByUserAndSheet(findUser, findSheet)) return true;
//        return false;


}
