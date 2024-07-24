package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.DifficultyCreateRequestDto;
import com.ssafy.los.backend.sheet.model.dto.request.DifficultyUpdateRequestDto;
import com.ssafy.los.backend.sheet.model.dto.response.DifficultyResponseDto;
import com.ssafy.los.backend.sheet.model.entity.Difficulty;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.repository.DifficultyRepository;
import com.ssafy.los.backend.sheet.model.repository.SheetRepository;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
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
    public Long saveDifficultyRating(Long sheetId, DifficultyCreateRequestDto difficultyCreateRequestDto) {
        User user = userRepository.findById(difficultyCreateRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 유저가 없습니다. id = " + difficultyCreateRequestDto.getUserId()));
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 악보가 없습니다. id = " + sheetId));

        Difficulty difficultyRating = Difficulty.builder()
                .user(user)
                .sheet(sheet)
                .contents(difficultyCreateRequestDto.getContents())
                .level(difficultyCreateRequestDto.getLevel())
                .build();

        return difficultyRatingRepository.save(difficultyRating).getId();
    }

    // 난이도 평가 수정
    @Override
    public Long updateDifficultyRating(Long difficultyId, DifficultyUpdateRequestDto difficultyUpdateRequestDto) {
        Difficulty findDifficultyRating = difficultyRatingRepository.findById(difficultyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 난이도 평가가 없습니다. id = " + difficultyId));

        findDifficultyRating.update(difficultyUpdateRequestDto.getLevel(), difficultyUpdateRequestDto.getContents());
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
    public List<DifficultyResponseDto> findDifficultyRating(Long sheetId) {
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
