package com.ssafy.los.backend.play.model.service;

import com.ssafy.los.backend.play.model.dto.request.MultiResultAfterDto;
import com.ssafy.los.backend.play.model.dto.request.MultiResultBeforeDto;
import com.ssafy.los.backend.play.model.dto.response.MultiResultListDto;
import com.ssafy.los.backend.play.model.entity.MultiPlayResult;
import com.ssafy.los.backend.play.model.repository.MultiPlayResultRepository;
import com.ssafy.los.backend.sheet.model.entity.Sheet;
import com.ssafy.los.backend.sheet.model.repository.SheetRepository;
import com.ssafy.los.backend.user.model.entity.User;
import com.ssafy.los.backend.user.model.repository.UserRepository;
import com.ssafy.los.backend.user.model.service.AuthService;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    // 게임이 종료되었을 떄, 결과 테이블 가져오기
    @Override
    public Long completeMultiPlayResult(Long multiResultId, MultiResultAfterDto multiResultAfterDto) {
        MultiPlayResult multiPlayResult = multiPlayResultRepository.findById(multiResultId)
                .orElseThrow(() -> new RuntimeException("multi play result not found"));

        User myUser = userRepository.findUserById(multiResultAfterDto.getMyUserId())
                .orElseThrow(() -> new RuntimeException("user not found"));

        User otherUser = userRepository.findUserById(multiResultAfterDto.getOtherUserId())
                .orElseThrow(() -> new RuntimeException("other user not found"));

        Float myScore = multiResultAfterDto.getMyScore();
        Float otherScore = multiResultAfterDto.getOtherScore();

        // TODO: 무승부에 대한 처리?
        if (myScore >= otherScore) {
            multiPlayResult.update(myUser, myScore, otherUser, otherScore);
        } else {
            multiPlayResult.update(otherUser, otherScore, myUser, myScore);
        }
        return multiResultId;
    }

    // 로그인한 유저에 대하여 멀티 결과 기록들을 모두 반환한다.
    @Override
    public List<MultiResultListDto> getMultiPlayResultList(User user) {
        List<MultiPlayResult> resultList = multiPlayResultRepository.findAllByUserOrderByCreatedAt(user);

        List<MultiResultListDto> resultListDtoList = new ArrayList<>();

        for (MultiPlayResult multiPlayResult : resultList) {
            User winner = multiPlayResult.getWinner();
            User loser = multiPlayResult.getLoser();
            boolean isWinner = winner.equals(user);
            MultiResultListDto result = MultiResultListDto.builder()
                    .myUsername(isWinner ? winner.getNickname() : loser.getNickname())
                    .myProfileUrl(isWinner ? winner.getUserImg() : loser.getUserImg())
                    .myScore(isWinner ? multiPlayResult.getWinnerScore() : multiPlayResult.getLoserScore())
                    .otherUsername(isWinner ? loser.getNickname() : winner.getNickname())
                    .otherScore(isWinner ? multiPlayResult.getLoserScore() : multiPlayResult.getWinnerScore())
                    .sheetTitle(multiPlayResult.getSheet().getTitle())
                    .sheetUrl(multiPlayResult.getSheet().getFileName())
                    .level(multiPlayResult.getSheet().getLevel()).build();
            resultListDtoList.add(result);
        }

        return resultListDtoList;

//        List<MultiPlayResult> winnerList = multiPlayResultRepository.findAllByWinner(user);
//        List<MultiPlayResult> loserList = multiPlayResultRepository.findAllByLoser(user);
//
//        List<MultiResultListDto> resultListDtoList = new ArrayList<>();
//        // 이긴 경기에 대한 멀티 결과
//        for (MultiPlayResult multiPlayResult : winnerList) {
//            User winner = multiPlayResult.getWinner();
//            User loser = multiPlayResult.getLoser();
//            MultiResultListDto result = MultiResultListDto.builder()
//                    .myUsername(winner.getNickname())
//                    .myProfileUrl(winner.getUserImg())
//                    .myScore(multiPlayResult.getWinnerScore())
//                    .otherUsername(loser.getNickname())
//                    .otherScore(multiPlayResult.getLoserScore())
//                    .sheetTitle(multiPlayResult.getSheet().getTitle())
//                    .sheetUrl(multiPlayResult.getSheet().getFileName())
//                    .level(multiPlayResult.getSheet().getLevel()).build();
//            resultListDtoList.add(result);
//        }
//
//        // 진 경기에 대한 멀티 결과
//        for (MultiPlayResult multiPlayResult : loserList) {
//            User winner = multiPlayResult.getWinner();
//            User loser = multiPlayResult.getLoser();
//            MultiResultListDto result = MultiResultListDto.builder()
//                    .myUsername(loser.getNickname())
//                    .myProfileUrl(loser.getUserImg())
//                    .myScore(multiPlayResult.getLoserScore())
//                    .otherUsername(winner.getNickname())
//                    .otherScore(multiPlayResult.getWinnerScore())
//                    .sheetTitle(multiPlayResult.getSheet().getTitle())
//                    .sheetUrl(multiPlayResult.getSheet().getFileName())
//                    .level(multiPlayResult.getSheet().getLevel()).build();
//            resultListDtoList.add(result);
//        }
//
//        return resultListDtoList;
    }

    // TODO: ADMIN 만 멀티 결과에 대해 삭제할 수 있다.
    @Override
    public Long removeMultiPlayResult(Long multiPlayResultId)
    {
        multiPlayResultRepository.deleteById(multiPlayResultId);
        return multiPlayResultId;
    }



}
