package com.ssafy.los.backend.play.model.service;

import com.ssafy.los.backend.play.model.dto.request.MultiResultAfterDto;
import com.ssafy.los.backend.play.model.dto.request.MultiResultBeforeDto;
import com.ssafy.los.backend.play.model.dto.request.SinglePlayRequestDto;
import com.ssafy.los.backend.play.model.dto.response.MultiPlayResultListDto;
import com.ssafy.los.backend.play.model.entity.MultiPlayResult;
import com.ssafy.los.backend.play.model.entity.SinglePlayResult;
import com.ssafy.los.backend.play.model.repository.MultiPlayResultRepository;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.repository.SheetRepository;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import com.ssafy.los.backend.user.model.service.AuthService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MultiPlayServiceImpl implements MultiPlayService {

    private final MultiPlayResultRepository multiPlayResultRepository;
    private final UserRepository userRepository;
    private final SheetRepository sheetRepository;
    private final AuthService authService;

    // 방장이 게임을 시작했을 때, multi_result 생성
    @Override
    public Long saveMultiPlayResult(MultiResultBeforeDto multiResultBeforeDto) {
        Sheet playSheet = sheetRepository.findById(multiResultBeforeDto.getSheetId())
                .orElseThrow(() -> new RuntimeException("sheet not found"));

        MultiPlayResult multiPlayResult = MultiPlayResult.builder()
                .sheet(playSheet).build();

        return multiPlayResultRepository.save(multiPlayResult).getId();
    }

    @Override
    public Long completeMultiPlayResult(Long multiResultId, MultiResultAfterDto multiResultAfterDto) {
        // 게임이 종료되었을 떄, 결과 테이블 가져오기
        MultiPlayResult multiPlayResult = multiPlayResultRepository.findById(multiResultId)
                .orElseThrow(() -> new RuntimeException("multi play result not found"));

        User myUser = userRepository.findUserById(multiResultAfterDto.getMyUserId())
                .orElseThrow(() -> new RuntimeException("user not found"));

        User otherUser = userRepository.findUserById(multiResultAfterDto.getOtherUserId())
                .orElseThrow(() -> new RuntimeException("other user not found"));

        Float myScore = multiResultAfterDto.getMyScore();
        Float otherScore = multiResultAfterDto.getOtherScore();

        // TODO: 무승부?
        if (myScore >= otherScore) {
            multiPlayResult.update(myUser, myScore, otherUser, otherScore);
        } else {
            multiPlayResult.update(otherUser, otherScore, myUser, myScore);
        }
        return multiResultId;
    }

    @Override
    public List<MultiPlayResultListDto> getMultiPlayResultList(Long multiResultId) {
        multiPlayResultRepository.findById(multiResultId);
        
        return null;
    }

    @Override
    public Long removeMultiPlayResult(Long MultiPlayResultId) {
        return 0;
    }



}
