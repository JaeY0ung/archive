package com.ssafy.los.backend.service.sheet;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.SheetStarRate;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.domain.repository.sheet.SheetStarRateRepository;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.sheet.request.SheetStarRateCreateDto;
import com.ssafy.los.backend.dto.sheet.request.SheetStarRateUpdateDto;
import com.ssafy.los.backend.dto.sheet.response.SheetStarRateDto;
import com.ssafy.los.backend.service.auth.AuthService;
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
    private final AuthService authService;

    // 별점 생성
    @Override
    public Long saveSheetStarRate(SheetStarRateCreateDto sheetStarRateCreateDto, Long sheetId) {
        User user = authService.getLoginUser();
        Sheet findSheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));

        SheetStarRate sheetStarRate = SheetStarRate.builder()
                .user(user)
                .sheet(findSheet)
                .content(sheetStarRateCreateDto.getContent())
                .starRate(sheetStarRateCreateDto.getStarRate())
                .build();

        return sheetStarRateRepository.save(sheetStarRate).getId();
    }

    // 별점 수정
    @Override
    public Long updateSheetStarRate(Long sheetStarRateId,
            SheetStarRateUpdateDto sheetStarRateUpdateDto) {
        SheetStarRate findSheetStarRate = sheetStarRateRepository.findById(sheetStarRateId)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 리뷰가 없습니다. id = " + sheetStarRateId));

        findSheetStarRate.update(sheetStarRateUpdateDto.getContent(),
                sheetStarRateUpdateDto.getStarRate());
        return sheetStarRateId;
    }

    // 별점 삭제
    @Override
    public Long deleteSheetStarRate(Long sheetStarRateId) {
        sheetStarRateRepository.deleteById(sheetStarRateId);
        return sheetStarRateId;
    }

    // 악보 기준 별점 목록 조회
    @Override
    public List<SheetStarRateDto> searchSheetStarRateBySheetId(Long sheetId) {
        Sheet sheet = sheetRepository.findById(sheetId)
                .orElseThrow(() -> new IllegalArgumentException("해당 악보가 없습니다. id = " + sheetId));
        List<SheetStarRate> sheetStarRatesList = sheetStarRateRepository.findAllBySheet(sheet);
        List<SheetStarRateDto> result = sheetStarRatesList.stream()
                .map(SheetStarRateDto::toEntity)
                .collect(Collectors.toList());
        return result;
    }

}
