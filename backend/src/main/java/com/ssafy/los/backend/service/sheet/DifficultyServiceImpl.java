package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.domain.entity.Difficulty;
import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.sheet.DifficultyRepository;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.sheet.request.DifficultyCreateDto;
import com.ssafy.los.backend.dto.sheet.request.DifficultyUpdateDto;
import com.ssafy.los.backend.dto.sheet.response.DifficultyPredictResponseDto;
import com.ssafy.los.backend.dto.sheet.response.DifficultyResponseDto;
import com.ssafy.los.backend.util.FileUploadUtil;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DifficultyServiceImpl implements DifficultyService {

    @Value("${cors.allowedOrigins.predict}")
    private String fastApiUrl;

    private final DifficultyRepository difficultyRatingRepository;
    private final SheetRepository sheetRepository;
    private final UserRepository userRepository;
    private final FileUploadUtil fileUploadUtil;
    private final DifficultyRepository difficultyRepository;
    private final RestTemplate restTemplate;


    // 난이도 평가 생성
    @Override
    public Long saveDifficulty(Long sheetId, Long userId, DifficultyCreateDto difficultyCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id = " + userId));
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));

        // 해당 악보 난이도 평가 기록 이력 파악
        boolean isExisted = difficultyRepository.existsByUserAndSheet(user, sheet);

        // TODO : 개발 테스트 완료하면 한 악보당 중복 체크 불가능하도록 만들기
//        if (isExisted) {
//            throw new IllegalStateException("이미 해당 악보에 대한 난이도 평가를 하셨습니다.");
//        }

        Difficulty difficulty = Difficulty.builder()
                .user(user)
                .sheet(sheet)
                .content(difficultyCreateDto.getContent())
                .level(difficultyCreateDto.getLevel())
                .build();

        // 난이도 업데이트
        calculateDifficulty(sheetId);

        return difficultyRatingRepository.save(difficulty).getId();
    }

    // 난이도 평가 수정
    @Override
    public Long updateDifficulty(Long difficultyId, DifficultyUpdateDto difficultyUpdateDto) {
        Difficulty findDifficultyRating = difficultyRatingRepository.findById(difficultyId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 난이도 평가가 없습니다. id = " + difficultyId));

        findDifficultyRating.update(difficultyUpdateDto.getLevel(),
                difficultyUpdateDto.getContents());

        // 난이도 업데이트
        calculateDifficulty(findDifficultyRating.getSheet().getId());

        return difficultyId;
    }

    // 난이도 평가 삭제
    @Override
    public Long deleteDifficulty(Long difficultyId) {
        Difficulty findDifficultyRating = difficultyRatingRepository.findById(difficultyId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 난이도 평가가 없습니다. id = " + difficultyId));

        // 난이도 업데이트
        calculateDifficulty(findDifficultyRating.getSheet().getId());

        difficultyRatingRepository.deleteById(difficultyId);

        return difficultyId;
    }

    // 악보 난이도 평가 목록 조회 (페이지 추가)
    @Override
    public Page<DifficultyResponseDto> searchDifficultyBySheetId(Long sheetId, int page, int size,
            String sortBy,
            String sortDir) {

        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));

        // pageable 객체 생성
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // 악보에 해당하는 난이도 평가들 반환
        Page<Difficulty> difficultyPage = difficultyRatingRepository.findAllBySheet(sheet,
                pageable);

        return difficultyPage.map(difficulty ->
                DifficultyResponseDto.toEntity(difficulty, fileUploadUtil));
    }

    // 악보 난이도 평가 목록 조회 (전체)
    @Override
    public List<DifficultyResponseDto> searchDifficultyAllBySheetId(Long sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new NoSuchElementException("해당 악보를 찾을 수 없습니다. id = " + sheetId));

        List<Difficulty> difficultyList = difficultyRatingRepository.findAllBySheet(sheet);

        if (difficultyList.isEmpty()) {
            return Collections.emptyList();
        }

        return difficultyList.stream()
                .map(difficulty -> DifficultyResponseDto.toEntity(difficulty, fileUploadUtil))
                .collect(Collectors.toList());
    }

    // 악보 난이도 AI 예측 호출
    @Override
    public int predictLevel(Sheet sheet) {
        String url = fastApiUrl + "/predict-difficulty";
        log.info("Sending request to predict difficulty for sheet: {}", sheet.getId());

        try {
            // 요청 본문 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestBody = String.format("{\"filename\": \"%s\"}", sheet.getUuid() + ".mid");
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // POST 요청 보내기
            ResponseEntity<DifficultyPredictResponseDto> response = restTemplate.postForEntity(
                    url, request, DifficultyPredictResponseDto.class);

            // 응답 처리
            if (response.getStatusCode().is2xxSuccessful()) {
                DifficultyPredictResponseDto predictionResponse = response.getBody();
                if (predictionResponse != null) {
                    int predictedDifficulty = predictionResponse.getPredicted_difficulty();
                    float predictionConfidence = predictionResponse.getPrediction_confidence();

                    log.info(
                            "Difficulty prediction successful for sheet {}: difficulty = {}, confidence = {}%",
                            sheet.getId(), predictedDifficulty, predictionConfidence);

                    // 악보 난이도 평가 생성 및 악보 레벨 업데이트
//                     saveDifficulty(sheet.getId(), 0L, new DifficultyCreateDto(predictedDifficulty, "")); // TODO : 프론트 로직
                    sheet.updateLevel(predictedDifficulty);

                    return predictedDifficulty;
                }
            }

            log.error("Failed to predict difficulty for sheet {}: {}", sheet.getId(),
                    response.getStatusCode());
            throw new RuntimeException("Failed to predict difficulty: " + response.getStatusCode());
        } catch (Exception e) {
            log.error("Error predicting difficulty for sheet {}: {}", sheet.getId(), e.getMessage(),
                    e);
            throw new RuntimeException("Error predicting difficulty: " + e.getMessage(), e);
        }
    }

    @Override
    public int predictLevelBySheetId(Long sheetId) {
        String url = fastApiUrl + "/predict-difficulty";
        log.info("Sending request to predict difficulty for sheet: {}", sheetId);

        Sheet findSheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보를 찾을 수 없습니다."));
        log.info("findSheet: {} {}", findSheet.getId(), findSheet.getUuid());

        try {
            // 요청 본문 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestBody = String.format("{\"filename\": \"%s\"}",
                    findSheet.getUuid() + ".mid");
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // POST 요청 보내기
            ResponseEntity<DifficultyPredictResponseDto> response = restTemplate.postForEntity(
                    url, request, DifficultyPredictResponseDto.class);

            // 응답 처리
            if (response.getStatusCode().is2xxSuccessful()) {
                DifficultyPredictResponseDto predictionResponse = response.getBody();
                if (predictionResponse != null) {
                    int predictedDifficulty = predictionResponse.getPredicted_difficulty();
                    float predictionConfidence = predictionResponse.getPrediction_confidence();

                    log.info(
                            "Difficulty prediction successful for sheet {}: difficulty = {}, confidence = {}%",
                            findSheet.getId(), predictedDifficulty, predictionConfidence);

                    // 악보 난이도 평가 생성 및 악보 레벨 업데이트
//                     saveDifficulty(sheet.getId(), 0L, new DifficultyCreateDto(predictedDifficulty, "")); // TODO : 프론트 로직
                    findSheet.updateLevel(predictedDifficulty);

                    return predictedDifficulty;
                }
            }

            log.error("Failed to predict difficulty for sheet {}: {}", findSheet.getId(),
                    response.getStatusCode());
            throw new RuntimeException("Failed to predict difficulty: " + response.getStatusCode());
        } catch (Exception e) {
            log.error("Error predicting difficulty for sheet {}: {}", findSheet.getId(),
                    e.getMessage(), e);
            throw new RuntimeException("Error predicting difficulty: " + e.getMessage(), e);
        }
    }


    // 악보 난이도 계산 조회 (등록, 삭제, 수정에서 반영되어야 함)
    // TODO : 평가가 하나도 없는 경우 로직 처리하기
    @Override
    public int calculateDifficulty(Long sheetId) {
        Sheet findSheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));

        List<Difficulty> difficultyList = difficultyRatingRepository.findAllBySheet(findSheet);

        // 난이도 의견이 있는 평가만 필터링하고 시간순으로 정렬
        List<Difficulty> validDifficulties = difficultyList.stream()
                .filter(d -> d.getLevel() != null)
                .sorted((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt()))
                .collect(Collectors.toList());

        int totalDifficulties = validDifficulties.size();
        if (totalDifficulties == 0) {
            return 1; // TODO : 평가가 하나도 없는 경우
        }

        int trimCount = (int) Math.round(totalDifficulties * 0.1);

        // 상위 10%와 하위 10% 제거
        List<Difficulty> trimmedDifficulties = validDifficulties.subList(
                trimCount,
                Math.max(trimCount, totalDifficulties - trimCount)
        );

        // 최근 날짜 구하기
        LocalDateTime mostRecentTime = trimmedDifficulties.get(0).getCreatedAt();

        double weightedSum = 0;
        double weightSum = 0; // 전체 가중치

        for (int i = 0; i < trimmedDifficulties.size(); i++) {
            Difficulty difficulty = trimmedDifficulties.get(i);
            double weight = calculateWeight(difficulty.getCreatedAt(), i, mostRecentTime,
                    trimmedDifficulties.size());
            weightedSum += difficulty.getLevel() * weight;
            weightSum += weight;
        }

        double averageDifficulty = weightSum > 0 ? weightedSum / weightSum : 0; // 양수 반환

        // 평균을 1-5 범위로 매핑
        int result = Math.min(Math.max((int) Math.round(averageDifficulty), 1), 5);
        log.info("result = {}", result);

        // 악보에 반영하기
        findSheet.updateLevel(result);
        return result;
    }

    private double calculateWeight(LocalDateTime opinionTime, int opinionIndex,
            LocalDateTime mostRecentTime,
            int totalOpinions) {
        long daysDifference = ChronoUnit.DAYS.between(opinionTime, mostRecentTime);
        int indexDifference = totalOpinions - opinionIndex - 1;

        double timeWeight = Math.pow(0.5, daysDifference / 180.0); // 반년 반감기
        double orderWeight = Math.pow(0.9, indexDifference); // 10%씩 감소

        return Math.max(timeWeight, orderWeight);
    }
}
