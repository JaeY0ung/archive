package com.ssafy.los.backend.service.play;

import com.ssafy.los.backend.domain.entity.MultiPlayResult;
import com.ssafy.los.backend.domain.entity.Sheet;
import com.ssafy.los.backend.domain.entity.User;
import com.ssafy.los.backend.domain.repository.play.MultiPlayResultRepository;
import com.ssafy.los.backend.domain.repository.sheet.SheetRepository;
import com.ssafy.los.backend.domain.repository.user.UserRepository;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultAfterDto;
import com.ssafy.los.backend.dto.play.request.MultiPlayResultBeforeDto;
import com.ssafy.los.backend.dto.play.response.MultiPlayResultProfileDto;
import com.ssafy.los.backend.service.auth.AuthService;
import com.ssafy.los.backend.service.sheet.SheetService;
import com.ssafy.los.backend.util.FileUploadUtil;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MultiPlayServiceImpl implements MultiPlayService {

    @Value("${fastapi.server.url}")
    private String fastApiServerUrl;

    private final MultiPlayResultRepository multiPlayResultRepository;
    private final UserRepository userRepository;
    private final SheetRepository sheetRepository;
    private final FileUploadUtil fileUploadUtil;
    private final SheetService sheetService;
    private final AuthService authService;

    // 방장이 게임을 시작했을 때, multi_result 생성
    @Override
    public Long saveMultiPlayResult(MultiPlayResultBeforeDto multiResultBeforeDto) {
        Sheet playSheet = sheetRepository.findById(multiResultBeforeDto.getSheetId())
                .orElseThrow(() -> new RuntimeException("sheet not found"));

        MultiPlayResult multiPlayResult = MultiPlayResult.builder()
                .sheet(playSheet).build();

        log.info("생성된 멀티 리저트 = {}", multiPlayResult.toString());
        log.info("생성된 멀티 리저트 = {}", multiPlayResult.getSheet().getTitle());

        return multiPlayResultRepository.save(multiPlayResult).getId();
    }

    // 게임이 종료되었을 떄, 결과 테이블 가져오기
    @Override
    public Long completeMultiPlayResult(Long multiResultId,
            MultiPlayResultAfterDto multiResultAfterDto) {

        log.info("멀티 리절트 아이디 = {}", multiResultId);
        MultiPlayResult multiPlayResult = multiPlayResultRepository.findById(multiResultId)
                .orElseThrow(() -> new RuntimeException("multi play result not found"));

        log.info("찾은 멀티 리저트 = {}", multiPlayResult.toString());

//        if (!multiPlayResult.isStatus()) {
        User myUser = userRepository.findUserByIdAndDeletedAtNull(
                        multiResultAfterDto.getMyUserId())
                .orElseThrow(() -> new RuntimeException("user not found"));

        User otherUser = userRepository.findByNicknameAndDeletedAtNull(
                        multiResultAfterDto.getOtherUserId())
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

        refreshMultiScoreOfUser(myUser.getId());
        refreshMultiScoreOfUser(otherUser.getId());

        log.info("최종으로 저장된 멀티 result = {}", multiPlayResult.toString());
        return multiResultId;
    }

    // 특정 유저에 대하여 멀티 결과 기록들을 모두 반환한다.
    @Override
    public List<MultiPlayResultProfileDto> getMultiPlayResultList(Long userId) {
        User findUser = userRepository.findUserByIdAndDeletedAtNull(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));
        List<MultiPlayResult> resultList = multiPlayResultRepository.findAllByUserOrderByCreatedAt(findUser);

        List<MultiPlayResultProfileDto> multiPlayResultProfileDtoList = new ArrayList<>();

        for (MultiPlayResult multiPlayResult : resultList) {
            User winner = multiPlayResult.getWinner();
            User loser = multiPlayResult.getLoser();
            boolean isWinner = winner.equals(findUser);

            MultiPlayResultProfileDto.MultiPlayResultProfileDtoBuilder builder = MultiPlayResultProfileDto.builder();

            if (isWinner && winner != null) {
                builder.myNickname(winner.getNickname())
                        .myProfileImgName(winner.getUserImg())
                        .myScore(multiPlayResult.getWinnerScore());
            } else if (!isWinner && loser != null) {
                builder.myNickname(loser.getNickname())
                        .myProfileImgName(loser.getUserImg())
                        .myScore(multiPlayResult.getLoserScore());
            }

            if (isWinner && loser != null) {
                builder.otherNickname(loser.getNickname())
                        .otherProfileImgName(loser.getUserImg())
                        .otherScore(multiPlayResult.getLoserScore());
            } else if (!isWinner && winner != null) {
                builder.otherNickname(winner.getNickname())
                        .otherProfileImgName(winner.getUserImg())
                        .otherScore(multiPlayResult.getWinnerScore());
            }

            if (multiPlayResult.getSheet() != null) {
                builder.sheetTitle(multiPlayResult.getSheet().getTitle())
                        .level(multiPlayResult.getSheet().getLevel());

                if (multiPlayResult.getSheet().getSong() != null) {
                    builder.songComposer(multiPlayResult.getSheet().getSong().getComposer())
                            .songImgName(multiPlayResult.getSheet().getSong().getImgName());
                }

                if (multiPlayResult.getSheet().getUploader() != null) {
                    builder.uploaderNickname(multiPlayResult.getSheet().getUploader().getNickname());
                } else {
                    builder.uploaderNickname("");
                }
            }

            builder.playTime(multiPlayResult.getPlayTime())
                    .draw(multiPlayResult.isDraw());

            MultiPlayResultProfileDto result = builder.build();

            try {
                result.loadUserImg(fileUploadUtil);
            } catch (Exception e) {
                log.warn("Failed to load user image for user: {}", findUser.getId());
            }

            try {
                result.loadSongImg(fileUploadUtil);
            } catch (Exception e) {
                log.warn("Failed to load song image for sheet: {}",
                        multiPlayResult.getSheet() != null ? multiPlayResult.getSheet().getId() : "unknown");
            }

            if (result != null) {
                multiPlayResultProfileDtoList.add(result);
            }
        }

        return multiPlayResultProfileDtoList;
    }

    @Override
    public Long removeMultiPlayResult(Long multiPlayResultId) {
        multiPlayResultRepository.deleteById(multiPlayResultId);
        return multiPlayResultId;
    }

    @Override
    public void refreshMultiScoreOfUser(Long userId) {
        Long winCount = multiPlayResultRepository.calculateCountOfWinMultiPlayResultByUserId(
                userId);
        Long loseCount = multiPlayResultRepository.calculateCountOfWinMultiPlayResultByUserId(
                userId);
        User user = userRepository.findById(userId).orElseThrow();
        long multiScore = 1000L + (winCount - loseCount) * 50;
        user.setRefreshMultiScore(Math.toIntExact(multiScore));
    }

    public String getLiveScore(MultipartFile file, Long sheetId, Long singleResultId)
            throws IllegalArgumentException {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(fastApiServerUrl + "/playing/single");
            log.info("fastAPI 요청 ={}", request);

            String userNickname = authService.getLoginUser().getNickname();

            HttpEntity multipart = MultipartEntityBuilder.create()
                    .addBinaryBody("file", file.getInputStream(),
                            ContentType.MULTIPART_FORM_DATA,
                            file.getOriginalFilename())
                    .addTextBody("uuid", sheetService.searchById(sheetId).getUuid(),
                            ContentType.TEXT_PLAIN)
                    .addTextBody("singleResultId", singleResultId.toString(),
                            ContentType.TEXT_PLAIN)
                    .addTextBody("nickname", userNickname,
                            ContentType.TEXT_PLAIN)
                    .build();

            request.setEntity(multipart);

            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                throw new IllegalArgumentException("[파일 계산 실패]");
            }

            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new IllegalArgumentException("[파일 계산 실패] " + e.getMessage());
        }
    }
}
