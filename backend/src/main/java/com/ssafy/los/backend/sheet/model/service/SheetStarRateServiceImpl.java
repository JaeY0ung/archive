package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateRequest;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.entity.SheetStarRate;
import com.ssafy.los.backend.sheet.model.repository.SheetRepository;
import com.ssafy.los.backend.sheet.model.repository.SheetStarRateRepository;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SheetStarRateServiceImpl implements SheetStarRateService {

    private final SheetStarRateRepository sheetStarRateRepository;
    private final UserRepository userRepository;
    private final SheetRepository sheetRepository;

    // 별점 저장
    @Override
    public Long saveStarRate(SheetStarRateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 유저가 없습니다. id = " + request.getUserId()));
        Sheet sheet = sheetRepository.findById(request.getSheetId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 악보가 없습니다. id = " + request.getSheetId()));

        SheetStarRate sheetStarRate = request.toEntity(user, sheet);

        return sheetStarRateRepository.save(sheetStarRate).getId();
    }

    // 별점 수정
    @Override
    public Long updateStarRate(Long id, SheetStarRateRequest request) {
        SheetStarRate findSheetStarRate = sheetStarRateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다. id = " + id));

        // 영속성 컨텍스트에서 관리 -> repsitory에 불러올 필요 없음
        findSheetStarRate.update(request.getContent(), request.getStarRate());
        return id;
    }

    // 별점 삭제
    @Override
    public Long deleteStarRate(Long id) {
        sheetStarRateRepository.deleteById(id);
        return id;
    }

    // 별점 하나 가져오기
    @Override
    public SheetStarRate findStarRateById(Long id) {
        return sheetStarRateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 별점이 없습니다. id = " + id));
    }

    @Override
    public List<SheetStarRate> findStarRateBySheetId(Long sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));
        return sheetStarRateRepository.findAllBySheet(sheet);
    }

}
