package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.domain.entity.Difficulty;
import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.sheet.DifficultyRepository;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.sheet.request.DifficultyCreateDto;
import com.ssafy.los.backend.dto.sheet.request.DifficultyUpdateDto;
import com.ssafy.los.backend.dto.sheet.response.DifficultyResponseDto;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DifficultyServiceImpl implements DifficultyService {

    private final DifficultyRepository difficultyRatingRepository;
    private final SheetRepository sheetRepository;
    private final UserRepository userRepository;


    // 난이도 평가 생성
    @Override
    public Long saveDifficulty(Long sheetId, Long userId, DifficultyCreateDto difficultyCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id = " + userId));
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));

        Difficulty difficultyRating = Difficulty.builder()
                .user(user)
                .sheet(sheet)
                .contents(difficultyCreateDto.getContents())
                .level(difficultyCreateDto.getLevel())
                .build();

        return difficultyRatingRepository.save(difficultyRating).getId();
    }

    // 난이도 평가 수정
    @Override
    public Long updateDifficulty(Long difficultyId, DifficultyUpdateDto difficultyUpdateDto) {
        Difficulty findDifficultyRating = difficultyRatingRepository.findById(difficultyId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 난이도 평가가 없습니다. id = " + difficultyId));

        findDifficultyRating.update(difficultyUpdateDto.getLevel(),
                difficultyUpdateDto.getContents());

        return difficultyId;
    }

    // 난이도 평가 삭제
    @Override
    public Long deleteDifficulty(Long difficultyId) {
        difficultyRatingRepository.deleteById(difficultyId);

        return difficultyId;
    }

    // 악보 난이도 평가 목록 조회
    @Override
    public List<DifficultyResponseDto> searchDifficultyBySheetId(Long sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));
        List<Difficulty> difficultyList = difficultyRatingRepository.findAllBySheet(sheet);

        List<DifficultyResponseDto> result = difficultyList.stream()
                .map(DifficultyResponseDto::toEntity)
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public int calculateDifficulty(Long sheetId) {
        Sheet findSheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));

        // 악보 난이도 계산 로직
        List<Difficulty> difficultyList = difficultyRatingRepository.findAllBySheet(findSheet);
        int result = difficultyList.stream()
                .map(Difficulty::getLevel)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        return result;
    }
}