package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.DifficultyRatingRequest;
import com.ssafy.los.backend.sheet.model.entity.DifficultyRating;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.repository.DifficultyRatingRepository;
import com.ssafy.los.backend.sheet.model.repository.SheetRepository;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DifficultyRatingServiceImpl implements DifficultyRatingService {

    private final DifficultyRatingRepository difficultyRatingRepository;
    private final SheetRepository sheetRepository;
    private final UserRepository userRepository;


    // 난이도 저장
    @Override
    public Long saveDifficultyRating(DifficultyRatingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 유저가 없습니다. id = " + request.getUserId()));
        Sheet sheet = sheetRepository.findById(request.getSheetId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 악보가 없습니다. id = " + request.getSheetId()));

        DifficultyRating difficultyRating = request.toEntity(user, sheet);

        return difficultyRatingRepository.save(difficultyRating).getId();
    }

    @Override
    public Long updateDifficultyRating(Long id, DifficultyRatingRequest request) {
        DifficultyRating findDifficultyRating = difficultyRatingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 난이도 평가가 없습니다. id = " + id));

        findDifficultyRating.update(request.getLevel(), request.getContents());
        return id;
    }

    @Override
    public Long deleteStarRate(Long id) {
        difficultyRatingRepository.deleteById(id);
        return id;
    }

    @Override
    public int findDifficultyRating(Long sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));
        List<DifficultyRating> difficultyRatings = difficultyRatingRepository.findAllBySheet(
                sheet);

        // 계산 로직
        int calculatedLevel = 987654321;

        return calculatedLevel;
    }
}
