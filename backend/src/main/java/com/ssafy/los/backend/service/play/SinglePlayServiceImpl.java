package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.SinglePlayResult;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.play.SinglePlayResultRepository;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.dto.play.request.SinglePlayRequestDto;
import com.ssafy.los.backend.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SinglePlayServiceImpl implements SinglePlayService {

    private final SinglePlayResultRepository singlePlayResultRepository;
    private final SheetRepository sheetRepository;
    private final AuthService authService;

    // 싱글 결과 생성
    @Override
    public Long saveSinglePlayResult(SinglePlayRequestDto singlePlayRequestDto) {
        User loginUser = authService.getLoginUser();
        Sheet sheetInfo = sheetRepository.findById(singlePlayRequestDto.getSheetId())
                .orElseThrow(() -> new RuntimeException("Sheet not found"));

        // singlePlayResult에 대한 결과 저장
        SinglePlayResult singlePlayResult = SinglePlayResult.builder()
                .user(loginUser)
                .sheet(sheetInfo)
                .score(singlePlayRequestDto.getScore())
                .build();

        // 플레이 종료시간 저장
        singlePlayResult.updatePlayTime();

        return singlePlayResultRepository.save(singlePlayResult).getId(); // single result 결과 저장
    }

    // 싱글 결과 목록 조회(유저, 악보 기준)
    @Override
    public Page<SinglePlayResult> getSinglePlayResultList(Pageable pageable) {
        Page<SinglePlayResult> page = singlePlayResultRepository.findAll(pageable);
        return page;
    }

    // 싱글 결과 삭제 : admin
    @Override
    public Long removeSinglePlayResult(Long singlePlayResultId) {
        singlePlayResultRepository.deleteById(singlePlayResultId);

        return singlePlayResultId;
    }
}
