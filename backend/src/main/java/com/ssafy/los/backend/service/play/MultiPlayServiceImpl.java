package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.domain.entity.MultiPlayResult;
import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.play.MultiPlayResultRepository;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultAfterDto;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultBeforeDto;
import com.ssafy.los.backend.dto.play.response.MultiPlayResultListDto;
import com.ssafy.los.backend.service.auth.AuthService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MultiPlayServiceImpl implements MultiPlayService {

    private final MultiPlayResultRepository multiPlayResultRepository;
    private final UserRepository userRepository;
    private final SheetRepository sheetRepository;
    private final AuthService authService;

    // 방장이 게임을 시작했을 때, multi_result 생성
    @Override
    public Long saveMultiPlayResult(MultiPlayResultBeforeDto multiResultBeforeDto) {
        Sheet playSheet = sheetRepository.findById(multiResultBeforeDto.getSheetId())
                .orElseThrow(() -> new RuntimeException("sheet not found"));

        MultiPlayResult multiPlayResult = MultiPlayResult.builder()
                .sheet(playSheet).build();

        return multiPlayResultRepository.save(multiPlayResult).getId();
    }

    // 게임이 종료되었을 떄, 결과 테이블 가져오기
    @Override
    public Long completeMultiPlayResult(Long multiResultId,
            MultiPlayResultAfterDto multiResultAfterDto) {

        MultiPlayResult multiPlayResult = multiPlayResultRepository.findById(multiResultId)
                .orElseThrow(() -> new RuntimeException("multi play result not found"));

        if (!multiPlayResult.isStatus()) {
            User myUser = userRepository.findUserById(multiResultAfterDto.getMyUserId())
                    .orElseThrow(() -> new RuntimeException("user not found"));

            User otherUser = userRepository.findUserById(multiResultAfterDto.getOtherUserId())
                    .orElseThrow(() -> new RuntimeException("other user not found"));

            Float myScore = multiResultAfterDto.getMyScore();
            Float otherScore = multiResultAfterDto.getOtherScore();

            if (myScore > otherScore) {
                multiPlayResult.update(myUser, myScore, otherUser, otherScore);
            } else if (myScore.equals(otherScore)) {
                multiPlayResult.update(myUser, myScore, otherUser, otherScore);
                multiPlayResult.updateDraw(true);
            } else {
                multiPlayResult.update(otherUser, otherScore, myUser, myScore);
            }

            // 게임이 종료되었으므로 상태 완료와 플레이 시간을 저장해준다.
            multiPlayResult.updatePlayTime();
            multiPlayResult.updateStatus(true);
        } else {
            // TODO: 이미 완료된 배틀 경기임
            log.info("이미 저장 완료된 배틀 기록입니다.");
        }

        return multiResultId;
    }

    // 로그인한 유저에 대하여 멀티 결과 기록들을 모두 반환한다.
    @Override
    public List<MultiPlayResultListDto> getMultiPlayResultList(User user) {
        List<MultiPlayResult> resultList = multiPlayResultRepository.findAllByUserOrderByCreatedAt(
                user);

        List<MultiPlayResultListDto> resultListDtoList = new ArrayList<>();

        for (MultiPlayResult multiPlayResult : resultList) {
            User winner = multiPlayResult.getWinner();
            User loser = multiPlayResult.getLoser();
            boolean isWinner = winner.equals(user);
            MultiPlayResultListDto result = MultiPlayResultListDto.builder()
                    .myNickname(isWinner ? winner.getNickname() : loser.getNickname())
                    .myProfileImg(isWinner ? winner.getUserImg() : loser.getUserImg())
                    .myScore(isWinner ? multiPlayResult.getWinnerScore()
                            : multiPlayResult.getLoserScore())
                    .otherNickname(isWinner ? loser.getNickname() : winner.getNickname())
                    .otherProfileImg(isWinner ? loser.getUserImg() : winner.getUserImg())
                    .otherScore(isWinner ? multiPlayResult.getLoserScore()
                            : multiPlayResult.getWinnerScore())
                    .sheetTitle(multiPlayResult.getSheet().getTitle())
                    .sheetUrl(multiPlayResult.getSheet().getFileName())
                    .level(multiPlayResult.getSheet().getLevel()).build();
            resultListDtoList.add(result);
        }

        return resultListDtoList;
    }

    // TODO: ADMIN 만 멀티 결과에 대해 삭제할 수 있다.
    @Override
    public Long removeMultiPlayResult(Long multiPlayResultId) {
        multiPlayResultRepository.deleteById(multiPlayResultId);
        return multiPlayResultId;
    }


}