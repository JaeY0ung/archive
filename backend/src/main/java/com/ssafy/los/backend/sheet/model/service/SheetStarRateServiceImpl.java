package com.ssafy.los.backend.sheet.model.service;

import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateCreateDto;
import com.ssafy.los.backend.sheet.model.dto.request.SheetStarRateUpdateDto;
import com.ssafy.los.backend.sheet.model.dto.response.SheetStarRateResponseDto;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.entity.SheetStarRate;
import com.ssafy.los.backend.sheet.model.repository.SheetRepository;
import com.ssafy.los.backend.sheet.model.repository.SheetStarRateRepository;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SheetStarRateServiceImpl implements SheetStarRateService {

    private final SheetStarRateRepository sheetStarRateRepository;
    private final UserRepository userRepository;
    private final SheetRepository sheetRepository;

    // 별점 생성
    @Override
    public Long saveStarRate(SheetStarRateCreateDto request, User user) {
        Sheet findSheet = sheetRepository.findById(request.getSheetId())
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + request.getSheetId()));

        SheetStarRate sheetStarRate = SheetStarRate.builder()
                .user(user)
                .sheet(findSheet)
                .content(request.getContent())
                .starRate(request.getStarRate())
                .build();

        return sheetStarRateRepository.save(sheetStarRate).getId();
    }

    // 별점 수정
    @Override
    public Long updateStarRate(Long sheetStarRateId, SheetStarRateUpdateDto sheetStarRateUpdateDto) {
        SheetStarRate findSheetStarRate = sheetStarRateRepository.findById(sheetStarRateId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다. id = " + sheetStarRateId));

        // 영속성 컨텍스트에서 수정
        findSheetStarRate.update(sheetStarRateUpdateDto.getContent(), sheetStarRateUpdateDto.getStarRate());
        return sheetStarRateId;
    }

    // 별점 삭제
    @Override
    public Long deleteStarRate(Long sheetStarRateId) {
        sheetStarRateRepository.deleteById(sheetStarRateId);
        return sheetStarRateId;
    }

    // 별점 하나 가져오기
    @Override
    public SheetStarRate findStarRateById(Long sheetStarRateId) {
        return sheetStarRateRepository.findById(sheetStarRateId)
                .orElseThrow(() -> new IllegalArgumentException("해당 별점이 없습니다. id = " + sheetStarRateId));
    }

    // 악보 기준 별점 목록 조회
    @Override
    public List<SheetStarRateResponseDto> findStarRateBySheetId(Long sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));
        List<SheetStarRate> sheetStarRatesList = sheetStarRateRepository.findAllBySheet(sheet);
        List<SheetStarRateResponseDto> result = sheetStarRatesList.stream()
                .map(SheetStarRateResponseDto::toEntity)
                .collect(Collectors.toList());
        return result;
    }

}
